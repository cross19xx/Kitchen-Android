package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.ai.TextEmbedder
import com.cross19xx.filmnest.ai.VectorMath
import com.cross19xx.filmnest.data.local.EmbeddingDao
import com.cross19xx.filmnest.data.local.MediaEmbeddingEntity
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.ScoredMedia
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmbeddingRepository @Inject constructor(
    private val embedder: TextEmbedder,
    private val dao: EmbeddingDao,
    private val genreRepository: GenreRepository,
) {
    private companion object {
        // Results below this cosine score are dropped — they're too weak to be "similar".
        const val MIN_SIMILARITY = 0.3f
    }

    // Genre names are fetched once and cached, so building text never re-hits the network.
    private var genreNames: Map<Int, String>? = null

    /**
     * Embed only if this item isn't already in the corpus — avoids redundant inference.
     */
    suspend fun embedIfAbsent(item: MediaItem) {
        if (!dao.existsById(item.id)) embedAndStore(item)
    }

    /** Build the text, embed it on-device, and persist the vector + snapshot. */
    suspend fun embedAndStore(item: MediaItem) {
        val vector = embedder.embed(buildText(item))
        dao.upsert(item.toEntity(vector))
    }

    /** Return the cached vector for an item, embedding + storing it first if it's missing. */
    suspend fun vectorFor(item: MediaItem): FloatArray {
        dao.getById(item.id)?.let { return it.vector }
        embedAndStore(item)
        return dao.getById(item.id)!!.vector
    }

    /** Embed free-text (e.g. a semantic search query). */
    suspend fun embedQuery(text: String): FloatArray = embedder.embed(text)

    /**
     * Rank the whole corpus against [toVector] by cosine similarity: drop [excludeId] (self),
     * drop anything below [MIN_SIMILARITY], sort best-first, and return the top [k].
     */
    suspend fun findSimilar(toVector: FloatArray, k: Int, excludeId: Int?): List<ScoredMedia> =
        dao.getAll().asSequence()
            .filter { it.mediaId != excludeId }
            .map {
                ScoredMedia(
                    it.toMediaItem(),
                    VectorMath.cosineSimilarity(toVector, it.vector)
                )
            }
            .filter { it.score >= MIN_SIMILARITY }
            .sortedByDescending { it.score }
            .take(k)
            .toList()

    private suspend fun buildText(item: MediaItem): String {
        val names = genreNamesFor(item.genreIds)
        return "${item.title}. Genres: ${names.joinToString(", ")}. ${item.overview}"
    }

    private suspend fun genreNamesFor(ids: List<Int>): List<String> {
        val map = genreNames ?: genreRepository.getGenres()
            .associate { it.id to it.name }
            .also { genreNames = it }
        return ids.mapNotNull { map[it] }
    }

    private fun MediaItem.toEntity(vector: FloatArray) = MediaEmbeddingEntity(
        mediaId = id,
        mediaType = mediaType,
        vector = vector,
        title = title,
        posterPath = posterPath,
        overview = overview,
        genreIds = genreIds,
        updatedAt = System.currentTimeMillis(),
    )
}