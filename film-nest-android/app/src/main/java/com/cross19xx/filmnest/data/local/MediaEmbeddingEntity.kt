package com.cross19xx.filmnest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaType

@Entity(tableName = "media_embeddings")
data class MediaEmbeddingEntity(
    @PrimaryKey val mediaId: Int,
    val mediaType: MediaType,
    val vector: FloatArray,
    val title: String,
    val posterPath: String?,
    val overview: String,
    val genreIds: List<Int>,
    val updatedAt: Long,
) {
    /**
     * Reconstruct a domain MediaItem from the cached snapshot so results render offline
     * without re-hitting TMDB. Fields not needed for list rendering default to sensible blanks.
     */
    fun toMediaItem(): MediaItem = MediaItem(
        id = mediaId,
        genreIds = genreIds,
        mediaType = mediaType,
        overview = overview,
        popularity = 0.0,
        title = title,
        voteAverage = 0.0,
        voteCount = 0,
        backdropPath = null,
        posterPath = posterPath,
        releaseDate = null,
    )

    /**
     * A data class with a FloatArray field generates a compiler warning, because Kotlin's
     * auto-generated equals/hashCode compare arrays by reference, not contents. We never
     * compare entities by value (we always look them up by mediaId), so identity-based
     * equality is correct and silences the warning.
     */
    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = mediaId
}
