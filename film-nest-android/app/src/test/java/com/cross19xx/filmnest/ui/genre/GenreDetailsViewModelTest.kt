package com.cross19xx.filmnest.ui.genre

import androidx.lifecycle.SavedStateHandle
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class GenreDetailsViewModelTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var genreRepository: GenreRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMovieDetail = Movie(
        id = 42,
        title = "Funny Movie",
        overview = "Overview",
        posterUrl = null,
        backdropUrl = null,
        releaseDate = "2024-01-01",
        voteAverage = 7.0,
        genreIds = listOf(28),
    )

    private val testGenre = Genre(id = 28, name = "Action")
    private val testMovies = listOf(
        testMovieDetail.copy(id = 1, title = "Movie A", voteAverage = 7.0),
        testMovieDetail.copy(id = 2, title = "Movie B", voteAverage = 8.0)
    )

    private fun createSavedStateHandle(genreId: Int): SavedStateHandle {
        return SavedStateHandle(mapOf("genreId" to genreId))
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        movieRepository = mockk()
        genreRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `both repos succeed emits Success with movies and genre`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } returns testMovies
        coEvery { genreRepository.getGenre(28) } returns testGenre

        val viewModel = GenreDetailsViewModel(
            createSavedStateHandle(28), movieRepository, genreRepository
        )

        viewModel.uiState.test {
            val success = expectMostRecentItem() as GenreDetailsUiState.Success
            assertEquals("Action", success.genre.name)
            assertEquals(2, success.movies.size)
        }
    }

    @Test
    fun `movie repository fails, emits Error`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } throws RuntimeException("Network Failed")
        coEvery { genreRepository.getGenre(28) } returns testGenre

        val viewModel = GenreDetailsViewModel(
            createSavedStateHandle(28), movieRepository, genreRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as GenreDetailsUiState.Error
            assertEquals("Network Failed", error.message)
        }
    }

    @Test
    fun `genre repository fails, emits Error`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } returns testMovies
        coEvery { genreRepository.getGenre(28) } throws RuntimeException("Genre not found")

        val viewModel = GenreDetailsViewModel(
            createSavedStateHandle(28), movieRepository, genreRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as GenreDetailsUiState.Error
            assertEquals("Genre not found", error.message)
        }
    }

    @Test
    fun `error with null message emits Unknown error`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } throws RuntimeException()
        coEvery { genreRepository.getGenre(28) } returns testGenre

        val viewModel = GenreDetailsViewModel(
            createSavedStateHandle(28), movieRepository, genreRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as GenreDetailsUiState.Error
            assertEquals("Unknown error", error.message)
        }
    }
}