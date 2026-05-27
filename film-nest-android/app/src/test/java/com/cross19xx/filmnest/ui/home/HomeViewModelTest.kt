package com.cross19xx.filmnest.ui.home

import app.cash.turbine.test
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.Movie
import com.cross19xx.filmnest.data.repository.GenreRepository
import com.cross19xx.filmnest.data.repository.MovieRepository
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

    private lateinit var movieRepository: MovieRepository
    private lateinit var genreRepository: GenreRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    private fun createMovie(id: Int) = Movie(
        id = id,
        title = "Movie $id",
        overview = "Overview",
        posterUrl = null,
        backdropUrl = null,
        releaseDate = "2024-01-01",
        voteAverage = 7.0,
        genreIds = listOf(28),
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        movieRepository = mockk<MovieRepository>()
        genreRepository = mockk<GenreRepository>()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadMovies success emits Success with all lists populated`() = runTest {
        val popular = listOf(createMovie(1), createMovie(2))
        val nowPlaying = listOf(createMovie(3))
        val topRated = listOf(createMovie(4), createMovie(5))
        val upcoming = listOf(createMovie(6))
        val genres = listOf(Genre(id = 28, name = "Action"))

        coEvery { movieRepository.getPopularMovies() } returns popular
        coEvery { movieRepository.getNowPlayingMovies() } returns nowPlaying
        coEvery { movieRepository.getTopRatedMovies() } returns topRated
        coEvery { movieRepository.getUpcomingMovies() } returns upcoming
        coEvery { genreRepository.getGenres() } returns genres

        val viewModel = HomeViewModel(movieRepository, genreRepository)

        viewModel.uiState.test {
            val success = expectMostRecentItem() as HomeUiState.Success
            assertEquals(2, success.popular.size)
            assertEquals(1, success.nowPlaying.size)
            assertEquals(2, success.topRated.size)
            assertEquals(1, success.upcoming.size)
            assertEquals("Action", success.genres[0].name)
        }
    }

    @Test
    fun `loadMovies error emits Error with exception message`() = runTest {
        coEvery { movieRepository.getPopularMovies() } throws RuntimeException("Network failed")
        coEvery { movieRepository.getNowPlayingMovies() } throws RuntimeException("Network failed")
        coEvery { movieRepository.getTopRatedMovies() } throws RuntimeException("Network failed")
        coEvery { movieRepository.getUpcomingMovies() } throws RuntimeException("Network failed")
        coEvery { genreRepository.getGenres() } throws RuntimeException("Network failed")

        val viewModel = HomeViewModel(movieRepository, genreRepository)

        viewModel.uiState.test {
            val error = expectMostRecentItem() as HomeUiState.Error
            assertEquals("Network failed", error.message)
        }
    }

    @Test
    fun `loadMovies error with null message emits Unknown error`() = runTest {
        coEvery { movieRepository.getPopularMovies() } throws RuntimeException()
        coEvery { movieRepository.getNowPlayingMovies() } throws RuntimeException()
        coEvery { movieRepository.getTopRatedMovies() } throws RuntimeException()
        coEvery { movieRepository.getUpcomingMovies() } throws RuntimeException()
        coEvery { genreRepository.getGenres() } throws RuntimeException()

        val viewModel = HomeViewModel(movieRepository, genreRepository)

        viewModel.uiState.test {
            val error = expectMostRecentItem() as HomeUiState.Error
            assertEquals("Unknown error", error.message)
        }
    }
}
