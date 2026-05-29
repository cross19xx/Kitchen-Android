package com.cross19xx.filmnest.ui.home

import app.cash.turbine.test
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.repository.GenreRepository
import com.cross19xx.filmnest.data.repository.MovieRepository
import com.cross19xx.filmnest.data.repository.TvShowRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var genreRepository: GenreRepository
    private lateinit var movieRepository: MovieRepository
    private lateinit var tvShowRepository: TvShowRepository


    private val testDispatcher = UnconfinedTestDispatcher()

    private fun createMediaItem(id: Int, mediaType: MediaType) = MediaItem(
        id = id,
        title = "Movie $id",
        overview = "Overview",
        posterPath = null,
        backdropPath = null,
        releaseDate = "2024-01-01",
        voteAverage = 7.0,
        genreIds = listOf(28),
        mediaType = mediaType,
        popularity = 92.8,
        voteCount = 203
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        genreRepository = mockk<GenreRepository>()
        movieRepository = mockk<MovieRepository>()
        tvShowRepository = mockk<TvShowRepository>()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovies success emits Success with all lists populated`() = runTest {
        val popularMovies = listOf(
            createMediaItem(1, MediaType.MOVIE),
            createMediaItem(2, MediaType.MOVIE)
        )
        val nowPlayingMovies = listOf(
            createMediaItem(3, MediaType.MOVIE),
            createMediaItem(4, MediaType.MOVIE)
        )
        val topRatedMovies = listOf(
            createMediaItem(5, MediaType.MOVIE),
            createMediaItem(6, MediaType.MOVIE)
        )
        val upcomingMovies = listOf(
            createMediaItem(7, MediaType.MOVIE),
            createMediaItem(8, MediaType.MOVIE)
        )
        val popularTvShows = listOf(
            createMediaItem(9, MediaType.TV_SHOW),
            createMediaItem(10, MediaType.TV_SHOW)
        )
        val topRatedTvShows = listOf(
            createMediaItem(11, MediaType.TV_SHOW),
            createMediaItem(12, MediaType.TV_SHOW)
        )
        val genres = listOf(Genre(id = 28, name = "Action"))

        coEvery { movieRepository.getPopularMovies() } returns popularMovies
        coEvery { tvShowRepository.getPopularTvShows() } returns popularTvShows
        coEvery { movieRepository.getNowPlayingMovies() } returns nowPlayingMovies
        coEvery { movieRepository.getTopRatedMovies() } returns topRatedMovies
        coEvery { tvShowRepository.getTopRatedTvShows() } returns topRatedTvShows
        coEvery { movieRepository.getUpcomingMovies() } returns upcomingMovies

        coEvery { genreRepository.getGenres() } returns genres

        val viewModel = HomeViewModel(
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val success = expectMostRecentItem() as HomeUiState.Success
            assertEquals(4, success.popular.size)
            assertEquals(2, success.nowPlaying.size)
            assertEquals(4, success.topRated.size)
            assertEquals(2, success.upcoming.size)
            assertEquals("Action", success.genres[0].name)
        }
    }

    @Test
    fun `loadMovies error emits Error with exception message`() = runTest {
        coEvery { movieRepository.getPopularMovies() } throws RuntimeException("Network failed")
        coEvery { tvShowRepository.getPopularTvShows() } throws RuntimeException("Network failed")
        coEvery { movieRepository.getNowPlayingMovies() } throws RuntimeException("Network failed")
        coEvery { movieRepository.getTopRatedMovies() } throws RuntimeException("Network failed")
        coEvery { tvShowRepository.getTopRatedTvShows() } throws RuntimeException("Network failed")
        coEvery { movieRepository.getUpcomingMovies() } throws RuntimeException("Network failed")
        coEvery { genreRepository.getGenres() } throws RuntimeException("Network failed")

        val viewModel = HomeViewModel(
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as HomeUiState.Error
            assertEquals("Network failed", error.message)
        }
    }

    @Test
    fun `loadMovies error with null message emits Unknown error`() = runTest {
        coEvery { movieRepository.getPopularMovies() } throws RuntimeException()
        coEvery { tvShowRepository.getPopularTvShows() } throws RuntimeException()
        coEvery { movieRepository.getNowPlayingMovies() } throws RuntimeException()
        coEvery { movieRepository.getTopRatedMovies() } throws RuntimeException()
        coEvery { tvShowRepository.getTopRatedTvShows() } throws RuntimeException()
        coEvery { movieRepository.getUpcomingMovies() } throws RuntimeException()
        coEvery { genreRepository.getGenres() } throws RuntimeException()

        val viewModel = HomeViewModel(
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as HomeUiState.Error
            assertEquals("Unknown error", error.message)
        }
    }
}
