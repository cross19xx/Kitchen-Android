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
        coEvery { api.getMovieGenres() } returns GenreListResponse(
            genres = listOf(
                GenreDto(id = 28, name = "Action"),
                GenreDto(id = 29, name = "Horror")
            )
        )
        coEvery { api.getTvGenres() } returns GenreListResponse(
            genres = listOf(
                GenreDto(id = 28, name = "Action"),
                GenreDto(id = 30, name = "Comedy")
            )
        )

        val result = repository.getGenres()

        assertEquals(3, result.size)

        // They are sorted alphabetically
        assertEquals(28, result[0].id)
        assertEquals("Action", result[0].name)

        assertEquals(30, result[1].id)
        assertEquals("Comedy", result[1].name)

        assertEquals(29, result[2].id)
        assertEquals("Horror", result[2].name)
    }

    @Test
    fun `getGenres can return an empty list`() = runTest {
        coEvery { api.getMovieGenres() } returns GenreListResponse(genres = listOf())
        coEvery { api.getTvGenres() } returns GenreListResponse(genres = listOf())

        val result = repository.getGenres()
        assertEquals(0, result.size)
    }

    @Test(expected = RuntimeException::class)
    fun `getMovieGenres propagates API exception`() = runTest {
        coEvery { api.getMovieGenres() } throws RuntimeException("Network error")
        coEvery { api.getTvGenres() } returns GenreListResponse(genres = listOf())

        repository.getGenres()
    }

    @Test(expected = RuntimeException::class)
    fun `getTvGenres propagates API exception`() = runTest {
        coEvery { api.getMovieGenres() } throws RuntimeException("Network error")
        coEvery { api.getTvGenres() } returns GenreListResponse(genres = listOf())

        repository.getGenres()
    }
}
