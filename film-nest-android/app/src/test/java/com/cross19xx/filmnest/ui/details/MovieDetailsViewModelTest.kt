package com.cross19xx.filmnest.ui.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MovieDetail
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
class MovieDetailsViewModelTest {

    private lateinit var movieRepository: MovieRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMovieDetail = MovieDetail(
        id = 42,
        title = "Inception",
        overview = "A mind-bending thriller",
        tagline = "Your mind is the scene of the crime",
        posterUrl = "https://image.tmdb.org/t/p/w500/poster.jpg",
        backdropUrl = "https://image.tmdb.org/t/p/w500/backdrop.jpg",
        releaseDate = "2010-07-16",
        voteAverage = 8.4,
        runtime = 148,
        budget = 160000000L,
        revenue = 836800000L,
        status = "Released",
        genres = listOf(Genre(id = 28, name = "Action")),
    )

    private fun createSavedStateHandle(movieId: Int): SavedStateHandle {
        return SavedStateHandle(mapOf("movieId" to movieId))
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        movieRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful fetch emits Success with movie detail`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } returns testMovieDetail

        val viewModel = MovieDetailsViewModel(createSavedStateHandle(42), movieRepository)

        viewModel.uiState.test {
            val success = expectMostRecentItem() as MovieDetailsUiState.Success
            assertEquals("Inception", success.movie.title)
            assertEquals(42, success.movie.id)
        }
    }

    @Test
    fun `repository error emits Error with message`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } throws RuntimeException("Not found")

        val viewModel = MovieDetailsViewModel(createSavedStateHandle(42), movieRepository)

        viewModel.uiState.test {
            val error = expectMostRecentItem() as MovieDetailsUiState.Error
            assertEquals("Not found", error.message)
        }
    }

    @Test
    fun `repository error with null message emits Unknown error`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } throws RuntimeException()

        val viewModel = MovieDetailsViewModel(createSavedStateHandle(42), movieRepository)

        viewModel.uiState.test {
            val error = expectMostRecentItem() as MovieDetailsUiState.Error
            assertEquals("Unknown error", error.message)
        }
    }
}
