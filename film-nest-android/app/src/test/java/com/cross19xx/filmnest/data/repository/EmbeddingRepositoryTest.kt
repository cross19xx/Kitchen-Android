package com.cross19xx.filmnest.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cross19xx.filmnest.ai.TextEmbedder
import com.cross19xx.filmnest.data.local.EmbeddingDao
import com.cross19xx.filmnest.data.local.FilmNestDatabase
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmbeddingRepositoryTest {

    private lateinit var db: FilmNestDatabase
    private lateinit var dao: EmbeddingDao
    private lateinit var embedder: TextEmbedder
    private lateinit var genreRepository: GenreRepository
    private lateinit var repository: EmbeddingRepository

    private fun item(id: Int, title: String = "T$id", genreIds: List<Int> = listOf(28)) =
        MediaItem(
            id = id, genreIds = genreIds, mediaType = MediaType.MOVIE, overview = "O$id",
            popularity = 0.0, title = title, voteAverage = 0.0, voteCount = 0,
            backdropPath = null, posterPath = "/p$id.jpg", releaseDate = null,
        )

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FilmNestDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.embeddingDao()
        embedder = mockk()
        genreRepository = mockk()
        coEvery { genreRepository.getGenres() } returns listOf(Genre(28, "Action"))
        repository = EmbeddingRepository(embedder, dao, genreRepository)
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `embedAndStore embeds title genres and overview then persists the vector`() = runTest {
        coEvery { embedder.embed(any()) } returns floatArrayOf(1f, 0f)

        repository.embedAndStore(item(1, title = "Heat", genreIds = listOf(28)))

        coVerify { embedder.embed("Heat. Genres: Action. O1") }
        assertEquals(1, dao.count())
    }

    @Test
    fun `embedIfAbsent skips items already embedded`() = runTest {
        coEvery { embedder.embed(any()) } returns floatArrayOf(1f, 0f)
        repository.embedAndStore(item(1))

        repository.embedIfAbsent(item(1))

        coVerify(exactly = 1) { embedder.embed(any()) }
    }

    @Test
    fun `findSimilar ranks by cosine similarity, excludes self, and applies the threshold`() = runTest {
        // Query points along +x. Store three items with controlled vectors.
        coEvery { embedder.embed("T1. Genres: Action. O1") } returns floatArrayOf(1f, 0f)    // identical -> 1.0
        coEvery { embedder.embed("T2. Genres: Action. O2") } returns floatArrayOf(0.7f, 0.7f) // ~0.707
        coEvery { embedder.embed("T3. Genres: Action. O3") } returns floatArrayOf(-1f, 0f)   // -1.0 (below threshold)
        repository.embedAndStore(item(1))
        repository.embedAndStore(item(2))
        repository.embedAndStore(item(3))

        val results = repository.findSimilar(floatArrayOf(1f, 0f), k = 10, excludeId = 1)

        // item 1 excluded; item 3 below MIN_SIMILARITY; only item 2 remains.
        assertEquals(listOf(2), results.map { it.item.id })
        assertTrue(results[0].score > 0.7f)
    }

    @Test
    fun `vectorFor embeds and stores when the item is not cached`() = runTest {
        coEvery { embedder.embed(any()) } returns floatArrayOf(0.6f, 0.8f)

        val vector = repository.vectorFor(item(9))

        assertEquals(0.6f, vector[0], 0.0001f)
        assertTrue(dao.existsById(9))
    }

    @Test
    fun `embedQuery delegates to the embedder`() = runTest {
        coEvery { embedder.embed("tense space thriller") } returns floatArrayOf(0f, 1f)

        val vector = repository.embedQuery("tense space thriller")

        assertEquals(1f, vector[1], 0.0001f)
    }
}
