package com.cross19xx.filmnest.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cross19xx.filmnest.data.model.MediaType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmbeddingDaoTest {
    private lateinit var db: FilmNestDatabase
    private lateinit var dao: EmbeddingDao

    private fun entity(id: Int, vector: FloatArray = floatArrayOf(0.1f, 0.2f)) =
        MediaEmbeddingEntity(
            mediaId = id,
            mediaType = MediaType.MOVIE,
            vector = vector,
            title = "Title $id",
            posterPath = "/p$id.jpg",
            overview = "Overview $id",
            genreIds = listOf(28, 12),
            updatedAt = 0L,
        )

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FilmNestDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.embeddingDao()
    }

    @After
    fun tearDown() = db.close()

    @Test
    fun `upsert then getById returns the stored row with its vector`() = runTest {
        dao.upsert(entity(1, floatArrayOf(0.5f, -0.5f, 0.25f)))

        val row = dao.getById(1)

        assertEquals(1, row?.mediaId)
        assertArrayEquals(floatArrayOf(0.5f, -0.5f, 0.25f), row?.vector, 0.0f)
        assertEquals(listOf(28, 12), row?.genreIds)
        assertEquals(MediaType.MOVIE, row?.mediaType)
    }

    @Test
    fun `upsert replaces an existing row`() = runTest {
        dao.upsert(entity(1, floatArrayOf(1f)))
        dao.upsert(entity(1, floatArrayOf(2f)))

        assertEquals(1, dao.count())
        assertArrayEquals(floatArrayOf(2f), dao.getById(1)?.vector, 0.0f)
    }

    @Test
    fun `existsById reflects presence`() = runTest {
        assertFalse(dao.existsById(1))
        dao.upsert(entity(1))
        assertTrue(dao.existsById(1))
    }

    @Test
    fun `getAll returns every row`() = runTest {
        dao.upsert(entity(1))
        dao.upsert(entity(2))
        assertEquals(2, dao.getAll().size)
    }
}
