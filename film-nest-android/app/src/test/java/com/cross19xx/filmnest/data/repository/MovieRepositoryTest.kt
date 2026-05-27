package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.remote.TmdbApiService
import com.cross19xx.filmnest.data.remote.dto.GenreDto
import com.cross19xx.filmnest.data.remote.dto.MovieDetailDto
import com.cross19xx.filmnest.data.remote.dto.MovieDto
import com.cross19xx.filmnest.data.remote.dto.MovieListResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class MovieRepositoryTest {

    private lateinit var api: TmdbApiService
    private lateinit var repository: MovieRepository

    @Before
    fun setup() {
        api = mockk<TmdbApiService>()
        repository = MovieRepository(api)
    }

    private fun createMovieDto(
        id: Int = 1,
        title: String = "Test Movie",
        posterPath: String? = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg",
    ) = MovieDto(
        id = id,
        title = title,
        overview = "A test movie",
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = "2024-01-15",
        voteAverage = 7.5,
        voteCount = 100,
        genreIds = listOf(28, 12),
        popularity = 85.0,
    )

    private fun createMovieListResponse(vararg movies: MovieDto) = MovieListResponse(
        page = 1,
        results = movies.toList(),
        totalPages = 1,
        totalResults = movies.size,
    )

    @Test
    fun `getPopularMovies maps MovieDto fields to Movie correctly`() = runTest {
        val dto = createMovieDto(id = 42, title = "Inception")
        coEvery { api.getPopularMovies(any()) } returns createMovieListResponse(dto)

        val result = repository.getPopularMovies()

        assertEquals(1, result.size)
        val movie = result[0]
        assertEquals(42, movie.id)
        assertEquals("Inception", movie.title)
        assertEquals("A test movie", movie.overview)
        assertEquals("2024-01-15", movie.releaseDate)
        assertEquals(7.5, movie.voteAverage, 0.01)
        assertEquals(listOf(28, 12), movie.genreIds)
    }

    @Test
    fun `getPopularMovies prepends base URL to poster and backdrop paths`() = runTest {
        val dto = createMovieDto(posterPath = "/abc.jpg", backdropPath = "/xyz.jpg")
        coEvery { api.getPopularMovies(any()) } returns createMovieListResponse(dto)

        val result = repository.getPopularMovies()
        val movie = result[0]

        assertNotNull(movie.posterUrl)
        assertNotNull(movie.backdropUrl)
        assert(movie.posterUrl!!.endsWith("/abc.jpg"))
        assert(movie.backdropUrl!!.endsWith("/xyz.jpg"))
    }

    @Test
    fun `null posterPath maps to null posterUrl`() = runTest {
        val dto = createMovieDto(posterPath = null, backdropPath = null)
        coEvery { api.getPopularMovies(any()) } returns createMovieListResponse(dto)

        val result = repository.getPopularMovies()

        assertNull(result[0].posterUrl)
        assertNull(result[0].backdropUrl)
    }

    @Test
    fun `getNowPlayingMovies delegates to API and maps result`() = runTest {
        val dto = createMovieDto(id = 10)
        coEvery { api.getNowPlayingMovies(any()) } returns createMovieListResponse(dto)

        val result = repository.getNowPlayingMovies()

        assertEquals(1, result.size)
        assertEquals(10, result[0].id)
    }

    @Test
    fun `getTopRatedMovies delegates to API and maps result`() = runTest {
        val dto = createMovieDto(id = 20)
        coEvery { api.getTopRatedMovies(any()) } returns createMovieListResponse(dto)

        val result = repository.getTopRatedMovies()

        assertEquals(1, result.size)
        assertEquals(20, result[0].id)
    }

    @Test
    fun `getUpcomingMovies delegates to API and maps result`() = runTest {
        val dto = createMovieDto(id = 30)
        coEvery { api.getUpcomingMovies(any()) } returns createMovieListResponse(dto)

        val result = repository.getUpcomingMovies()

        assertEquals(1, result.size)
        assertEquals(30, result[0].id)
    }

    @Test
    fun `getMoviesByGenre delegates to API with genreId and maps result`() = runTest {
        val dto = createMovieDto(id = 40)
        coEvery { api.getMoviesByGenre(any(), any()) } returns createMovieListResponse(dto)

        val result = repository.getMoviesByGenre(genreId = 28)

        assertEquals(1, result.size)
        assertEquals(40, result[0].id)
    }

    @Test
    fun `getMovieDetails maps MovieDetailDto to MovieDetail`() = runTest {
        val detailDto = MovieDetailDto(
            id = 42,
            title = "Inception",
            overview = "A mind-bending thriller",
            tagline = "Your mind is the scene of the crime",
            posterPath = "/inception_poster.jpg",
            backdropPath = "/inception_backdrop.jpg",
            releaseDate = "2010-07-16",
            voteAverage = 8.4,
            voteCount = 30000,
            runtime = 148,
            budget = 160000000L,
            revenue = 836800000L,
            status = "Released",
            genres = listOf(GenreDto(id = 28, name = "Action"), GenreDto(id = 878, name = "Sci-Fi")),
            popularity = 95.0,
        )
        coEvery { api.getMovieDetails(42) } returns detailDto

        val result = repository.getMovieDetails(42)

        assertEquals(42, result.id)
        assertEquals("Inception", result.title)
        assertEquals("A mind-bending thriller", result.overview)
        assertEquals("Your mind is the scene of the crime", result.tagline)
        assert(result.posterUrl!!.endsWith("/inception_poster.jpg"))
        assert(result.backdropUrl!!.endsWith("/inception_backdrop.jpg"))
        assertEquals("2010-07-16", result.releaseDate)
        assertEquals(8.4, result.voteAverage, 0.01)
        assertEquals(148, result.runtime)
        assertEquals(160000000L, result.budget)
        assertEquals(836800000L, result.revenue)
        assertEquals("Released", result.status)
        assertEquals(2, result.genres.size)
        assertEquals("Action", result.genres[0].name)
        assertEquals("Sci-Fi", result.genres[1].name)
    }

    @Test(expected = RuntimeException::class)
    fun `getPopularMovies propagates API exception`() = runTest {
        coEvery { api.getPopularMovies(any()) } throws RuntimeException("Network error")

        repository.getPopularMovies()
    }
}
