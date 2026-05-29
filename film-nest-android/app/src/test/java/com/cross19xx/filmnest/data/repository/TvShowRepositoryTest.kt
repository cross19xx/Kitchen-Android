package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.remote.TmdbApiService
import com.cross19xx.filmnest.data.remote.dto.GenreDto
import com.cross19xx.filmnest.data.remote.dto.TvShowDetailDto
import com.cross19xx.filmnest.data.remote.dto.TvShowDto
import com.cross19xx.filmnest.data.remote.dto.TvShowListResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class TvShowRepositoryTest {
    private lateinit var api: TmdbApiService
    private lateinit var repository: TvShowRepository

    @Before
    fun setup() {
        api = mockk<TmdbApiService>()
        repository = TvShowRepository(api)
    }

    private fun createTvShowDto(
        id: Int = 1,
        name: String = "Test Show",
        posterPath: String? = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg",
    ) = TvShowDto(
        id = id,
        name = name,
        overview = "A test show",
        posterPath = posterPath,
        backdropPath = backdropPath,
        releaseDate = "2024-03-10",
        voteAverage = 8.0,
        voteCount = 200,
        genreIds = listOf(18, 10765),
        popularity = 90.0,
    )

    private fun createTvShowListResponse(vararg shows: TvShowDto) = TvShowListResponse(
        page = 1,
        results = shows.toList(),
        totalPages = 1,
        totalResults = shows.size,
    )

    @Test
    fun `getPopularTvShows maps TvShowDto fields to MediaItem correctly`() = runTest {
        val dto = createTvShowDto(id = 42, name = "Breaking Bad")
        coEvery { api.getPopularTvShows(any()) } returns createTvShowListResponse(dto)

        val result = repository.getPopularTvShows()

        assertEquals(1, result.size)
        val show = result[0]
        assertEquals(42, show.id)
        assertEquals("Breaking Bad", show.title)
        assertEquals("A test show", show.overview)
        assertEquals("2024-03-10", show.releaseDate)
        assertEquals(8.0, show.voteAverage, 0.01)
        assertEquals(listOf(18, 10765), show.genreIds)
        assertEquals(MediaType.TV_SHOW, show.mediaType)
    }

    @Test
    fun `getPopularTvShows prepends base URL to poster and backdrop paths`() = runTest {
        val dto = createTvShowDto(posterPath = "/abc.jpg", backdropPath = "/xyz.jpg")
        coEvery { api.getPopularTvShows(any()) } returns createTvShowListResponse(dto)

        val result = repository.getPopularTvShows()
        val show = result[0]

        assertNotNull(show.posterPath)
        assertNotNull(show.backdropPath)
        assert(show.posterPath!!.endsWith("/abc.jpg"))
        assert(show.backdropPath!!.endsWith("/xyz.jpg"))
    }

    @Test
    fun `null posterPath maps to null`() = runTest {
        val dto = createTvShowDto(posterPath = null, backdropPath = null)
        coEvery { api.getPopularTvShows(any()) } returns createTvShowListResponse(dto)

        val result = repository.getPopularTvShows()

        assertNull(result[0].posterPath)
        assertNull(result[0].backdropPath)
    }

    @Test
    fun `getTopRatedTvShows delegates to API and maps result`() = runTest {
        val dto = createTvShowDto(id = 20)
        coEvery { api.getTopRatedTvShows(any()) } returns createTvShowListResponse(dto)

        val result = repository.getTopRatedTvShows()

        assertEquals(1, result.size)
        assertEquals(20, result[0].id)
    }

    @Test
    fun `getTvShowsByGenre delegates to API with genreId and maps result`() = runTest {
        val dto = createTvShowDto(id = 40)
        coEvery { api.getTvShowsByGenre(any(), any()) } returns createTvShowListResponse(dto)

        val result = repository.getTvShowsByGenre(genreId = 18)

        assertEquals(1, result.size)
        assertEquals(40, result[0].id)
    }

    @Test
    fun `getTvShowDetails maps TvShowDetailDto to MediaItemDetail`() = runTest {
        val detailDto = TvShowDetailDto(
            id = 42,
            name = "Breaking Bad",
            overview = "A chemistry teacher turned meth manufacturer",
            tagline = "All hail the king",
            posterPath = "/bb_poster.jpg",
            backdropPath = "/bb_backdrop.jpg",
            firstAirDate = "2008-01-20",
            episodeRunTime = listOf(47),
            numberOfSeasons = 5,
            numberOfEpisodes = 62,
            voteAverage = 9.5,
            voteCount = 50000,
            lastAirDate = "2013-09-29",
            popularity = 120.0,
            status = "Ended",
            genres = listOf(
                GenreDto(id = 18, name = "Drama"),
                GenreDto(id = 80, name = "Crime")
            ),
        )
        coEvery { api.getTvShowDetails(42) } returns detailDto

        val result = repository.getTvShowDetails(42)

        assertEquals(42, result.id)
        assertEquals("Breaking Bad", result.title)
        assertEquals("A chemistry teacher turned meth manufacturer", result.overview)
        assertEquals("All hail the king", result.tagline)
        assert(result.posterPath!!.endsWith("/bb_poster.jpg"))
        assert(result.backdropPath!!.endsWith("/bb_backdrop.jpg"))
        assertEquals("2008-01-20", result.releaseDate)
        assertEquals(47, result.runtimeMinutes)
        assertEquals(9.5, result.voteAverage, 0.01)
        assertEquals("Ended", result.status)
        assertEquals(MediaType.TV_SHOW, result.mediaType)
        assertEquals(5, result.numberOfSeasons)
        assertEquals(62, result.numberOfEpisodes)
        assertEquals("2013-09-29", result.lastAirDate)
        assertEquals(2, result.genres.size)
        assertEquals("Drama", result.genres[0].name)
    }

    @Test(expected = RuntimeException::class)
    fun `getPopularTvShows propagates API exception`() = runTest {
        coEvery { api.getPopularTvShows(any()) } throws RuntimeException("Network error")

        repository.getPopularTvShows()
    }
}