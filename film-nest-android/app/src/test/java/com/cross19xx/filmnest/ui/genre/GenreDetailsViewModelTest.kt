package com.cross19xx.filmnest.ui.genre

import androidx.lifecycle.SavedStateHandle
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class GenreDetailsViewModelTest {

    private lateinit var genreRepository: GenreRepository
    private lateinit var movieRepository: MovieRepository
    private lateinit var tvShowRepository: TvShowRepository

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMovieItem = MediaItem(
        id = 42,
        title = "Funny Movie",
        overview = "The quick brown fox jumps over the lazy dog",
        posterPath = null,
        backdropPath = null,
        releaseDate = "2024-01-01",
        voteAverage = 7.0,
        genreIds = listOf(28),
        popularity = 20.8,
        voteCount = 203,
        mediaType = MediaType.MOVIE
    )

    private val testTvShowItem = MediaItem(
        id = 43,
        title = "Funny TV Show",
        backdropPath = null,
        posterPath = null,
        popularity = 2.5,
        voteCount = 200,
        mediaType = MediaType.TV_SHOW,
        voteAverage = 20.3,
        genreIds = listOf(28),
        releaseDate = "2026-03-03",
        overview = "The quick brown fox jumps over the lazy dog",
    )

    private val testGenre = Genre(id = 28, name = "Action")
    private val testMovies = listOf(
        testMovieItem.copy(id = 1, title = "Movie A", voteAverage = 7.0),
        testMovieItem.copy(id = 2, title = "Movie B", voteAverage = 8.0)
    )
    private val testTvShows = listOf(
        testTvShowItem.copy(id = 4, title = "Series 1"),
        testTvShowItem.copy(id = 5, title = "Series 2")
    )

    private fun createSavedStateHandle(genreId: Int): SavedStateHandle {
        return SavedStateHandle(mapOf("genreId" to genreId))
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        movieRepository = mockk()
        genreRepository = mockk()
        tvShowRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `all three repos succeed emits Success with movies, tv shows, and genre`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } returns testMovies
        coEvery { genreRepository.getGenre(28) } returns testGenre
        coEvery { tvShowRepository.getTvShowsByGenre(28) } returns testTvShows

        val viewModel = GenreDetailsViewModel(
            savedStateHandle = createSavedStateHandle(28),
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository,
        )

        viewModel.uiState.test {
            val success = expectMostRecentItem() as GenreDetailsUiState.Success
            assertEquals("Action", success.genre.name)
            assertEquals(4, success.mediaItems.size)
        }
    }

    @Test
    fun `movie repository fails, emits Error`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } throws RuntimeException("Network Failed")
        coEvery { genreRepository.getGenre(28) } returns testGenre
        coEvery { tvShowRepository.getTvShowsByGenre(28) } returns testTvShows

        val viewModel = GenreDetailsViewModel(
            savedStateHandle = createSavedStateHandle(28),
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository,
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as GenreDetailsUiState.Error
            assertEquals("Network Failed", error.message)
        }
    }

    @Test
    fun `genre repository fails, emits Error`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } returns testMovies
        coEvery { tvShowRepository.getTvShowsByGenre(28) } returns testTvShows
        coEvery { genreRepository.getGenre(28) } throws RuntimeException("Genre not found")

        val viewModel = GenreDetailsViewModel(
            savedStateHandle = createSavedStateHandle(28),
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository,
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as GenreDetailsUiState.Error
            assertEquals("Genre not found", error.message)
        }
    }

    @Test
    fun `tvShows repository fails, emits Error`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } returns testMovies
        coEvery { genreRepository.getGenre(28) } returns testGenre
        coEvery { tvShowRepository.getTvShowsByGenre(28) } throws RuntimeException("TV shows not found")

        val viewModel = GenreDetailsViewModel(
            savedStateHandle = createSavedStateHandle(28),
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository,
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as GenreDetailsUiState.Error
            assertEquals("TV shows not found", error.message)
        }
    }

    @Test
    fun `error with null message emits Unknown error`() = runTest {
        coEvery { movieRepository.getMoviesByGenre(28) } throws RuntimeException()
        coEvery { genreRepository.getGenre(28) } returns testGenre
        coEvery { tvShowRepository.getTvShowsByGenre(28) } returns testTvShows

        val viewModel = GenreDetailsViewModel(
            savedStateHandle = createSavedStateHandle(28),
            genreRepository = genreRepository,
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository,
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as GenreDetailsUiState.Error
            assertEquals("Unknown error", error.message)
        }
    }
}