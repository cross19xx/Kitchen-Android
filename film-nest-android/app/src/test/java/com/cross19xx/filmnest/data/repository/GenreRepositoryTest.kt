package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.remote.TmdbApiService
import com.cross19xx.filmnest.data.remote.dto.GenreDto
import com.cross19xx.filmnest.data.remote.dto.GenreListResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GenreRepositoryTest {

    private lateinit var api: TmdbApiService
    private lateinit var repository: GenreRepository

    @Before
    fun setup() {
        api = mockk<TmdbApiService>()
        repository = GenreRepository(api)
    }

    @Test
    fun `getGenres maps GenreDto list to Genre list`() = runTest {
        /*
         * Every test needs its own mock of values
         * Using `coEvery` because we are working with suspense functions. regular functions
         * use `every`
         */
        coEvery { api.getGenres() } returns GenreListResponse(
            genres = listOf(
                GenreDto(id = 28, name = "Action"),
                GenreDto(id = 29, name = "Horror")
            )
        )

        val result = repository.getGenres()

        assertEquals(2, result.size)

        assertEquals(28, result[0].id)
        assertEquals("Action", result[0].name)

        assertEquals(29, result[1].id)
        assertEquals("Horror", result[1].name)
    }

    @Test
    fun `getGenres can return an empty list`() = runTest {
        coEvery { api.getGenres() } returns GenreListResponse(genres = listOf())

        val result = repository.getGenres()
        assertEquals(0, result.size)
    }

    @Test(expected = RuntimeException::class)
    fun `getGenres propagates API exception`() = runTest {
        coEvery { api.getGenres() } throws RuntimeException("Network error")

        repository.getGenres()
    }
}
