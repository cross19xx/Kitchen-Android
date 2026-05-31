package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.model.SearchResult
import com.cross19xx.filmnest.data.remote.TmdbApiService
import com.cross19xx.filmnest.data.remote.dto.SearchMultiResponse
import com.cross19xx.filmnest.data.remote.dto.SearchResultDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchRepositoryTest {

    private lateinit var api: TmdbApiService
    private lateinit var repository: SearchRepository

    @Before
    fun setup() {
        api = mockk<TmdbApiService>()
        repository = SearchRepository(api)
    }

    private fun createDto(
        id: Int = 1,
        mediaType: String = "movie",
        title: String? = "Test Movie",
        name: String? = null,
        posterPath: String? = "/poster.jpg",
        backdropPath: String? = "/backdrop.jpg",
        profilePath: String? = null,
        releaseDate: String? = "2024-01-15",
        firstAirDate: String? = null,
        voteAverage: Double? = 7.5,
        voteCount: Int? = 100,
        genreIds: List<Int>? = listOf(28, 12),
    ) = SearchResultDto(
        id = id,
        mediaType = mediaType,
        title = title,
        name = name,
        overview = "An overview",
        posterPath = posterPath,
        backdropPath = backdropPath,
        profilePath = profilePath,
        releaseDate = releaseDate,
        firstAirDate = firstAirDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genreIds = genreIds,
    )

    private fun response(vararg dtos: SearchResultDto) = SearchMultiResponse(
        page = 1,
        results = dtos.toList(),
        totalPages = 1,
        totalResults = dtos.size,
    )

    @Test
    fun `searchMulti maps a movie dto to Media with MOVIE type`() = runTest {
        coEvery { api.searchMulti(any(), any()) } returns response(
            createDto(id = 11, mediaType = "movie", title = "Star Wars")
        )

        val result = repository.searchMulti("star")

        assertEquals(1, result.size)
        val media = result[0] as SearchResult.Media
        assertEquals(11, media.item.id)
        assertEquals("Star Wars", media.item.title)
        assertEquals(MediaType.MOVIE, media.item.mediaType)
    }

    @Test
    fun `searchMulti maps a tv dto to Media using name and firstAirDate`() = runTest {
        coEvery { api.searchMulti(any(), any()) } returns response(
            createDto(
                id = 253,
                mediaType = "tv",
                title = null,
                name = "Star Trek",
                releaseDate = null,
                firstAirDate = "1966-09-08",
            )
        )

        val result = repository.searchMulti("star")

        val media = result[0] as SearchResult.Media
        assertEquals("Star Trek", media.item.title)
        assertEquals(MediaType.TV_SHOW, media.item.mediaType)
        assertEquals("1966-09-08", media.item.releaseDate)
    }

    @Test
    fun `searchMulti maps a person dto to Person`() = runTest {
        coEvery { api.searchMulti(any(), any()) } returns response(
            createDto(
                id = 500,
                mediaType = "person",
                title = null,
                name = "Tom Cruise",
                posterPath = null,
                profilePath = "/tomcruise.jpg",
            )
        )

        val result = repository.searchMulti("tom")

        val person = result[0] as SearchResult.Person
        assertEquals(500, person.id)
        assertEquals("Tom Cruise", person.name)
        assertTrue(person.profilePath!!.endsWith("/tomcruise.jpg"))
    }

    @Test
    fun `searchMulti drops unknown media types`() = runTest {
        coEvery { api.searchMulti(any(), any()) } returns response(
            createDto(id = 1, mediaType = "movie"),
            createDto(id = 2, mediaType = "collection"),
            createDto(id = 3, mediaType = "person", title = null, name = "Someone"),
        )

        val result = repository.searchMulti("x")

        assertEquals(2, result.size)
        assertTrue(result[0] is SearchResult.Media)
        assertTrue(result[1] is SearchResult.Person)
    }

    @Test
    fun `searchMulti prepends base url to poster and backdrop paths`() = runTest {
        coEvery { api.searchMulti(any(), any()) } returns response(
            createDto(posterPath = "/abc.jpg", backdropPath = "/xyz.jpg")
        )

        val media = repository.searchMulti("x")[0] as SearchResult.Media

        assertTrue(media.item.posterPath!!.endsWith("/abc.jpg"))
        assertTrue(media.item.backdropPath!!.endsWith("/xyz.jpg"))
    }

    @Test
    fun `searchMulti defaults null numeric and list fields`() = runTest {
        coEvery { api.searchMulti(any(), any()) } returns response(
            createDto(voteAverage = null, voteCount = null, genreIds = null)
        )

        val media = repository.searchMulti("x")[0] as SearchResult.Media

        assertEquals(0.0, media.item.voteAverage, 0.01)
        assertEquals(0, media.item.voteCount)
        assertEquals(emptyList<Int>(), media.item.genreIds)
    }

    @Test
    fun `searchMulti returns empty list when there are no results`() = runTest {
        coEvery { api.searchMulti(any(), any()) } returns response()

        assertEquals(0, repository.searchMulti("x").size)
    }

    @Test(expected = RuntimeException::class)
    fun `searchMulti propagates API exception`() = runTest {
        coEvery { api.searchMulti(any(), any()) } throws RuntimeException("Network error")

        repository.searchMulti("x")
    }
}
