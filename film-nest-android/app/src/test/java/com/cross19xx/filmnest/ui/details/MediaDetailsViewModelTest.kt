package com.cross19xx.filmnest.ui.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MediaItemDetail
import com.cross19xx.filmnest.data.model.MediaType
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
class MediaDetailsViewModelTest {

    private lateinit var movieRepository: MovieRepository
    private lateinit var tvShowRepository: TvShowRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMovieDetail = MediaItemDetail(
        id = 42,
        title = "Inception",
        originalTitle = "Inception",
        overview = "A mind-bending thriller",
        tagline = "Your mind is the scene of the crime",
        posterPath = "https://image.tmdb.org/t/p/w500/poster.jpg",
        backdropPath = "https://image.tmdb.org/t/p/w500/backdrop.jpg",
        releaseDate = "2010-07-16",
        voteAverage = 8.4,
        voteCount = 200,
        runtimeMinutes = 148,
        budget = 160000000L,
        revenue = 836800000L,
        status = "Released",
        genres = listOf(Genre(id = 28, name = "Action")),
        mediaType = MediaType.MOVIE,
        popularity = 98.8,
        homepage = ""
    )

    private val testTvShwDetail = testMovieDetail.copy(mediaType = MediaType.TV_SHOW)

    private fun createSavedStateHandle(mediaId: Int, mediaType: MediaType): SavedStateHandle {
        return SavedStateHandle(
            mapOf(
                "mediaId" to mediaId,
                "mediaType" to mediaType
            ),
        )
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        movieRepository = mockk()
        tvShowRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `successful fetch emits Success with movie detail`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } returns testMovieDetail
        coEvery { tvShowRepository.getTvShowDetails(43) } returns testTvShwDetail.copy(id = 43)

        val viewModel = MediaDetailsViewModel(
            savedStateHandle = createSavedStateHandle(42, MediaType.MOVIE),
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val success = expectMostRecentItem() as MediaDetailsUiState.Success
            assertEquals("Inception", success.media.title)
            assertEquals(42, success.media.id)
        }
    }

    @Test
    fun `successful fetch emits Success with tv show detail`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } returns testMovieDetail
        coEvery { tvShowRepository.getTvShowDetails(43) } returns testTvShwDetail.copy(id = 43)

        val viewModel = MediaDetailsViewModel(
            savedStateHandle = createSavedStateHandle(43, MediaType.TV_SHOW),
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val success = expectMostRecentItem() as MediaDetailsUiState.Success
            assertEquals("Inception", success.media.title)
            assertEquals(43, success.media.id)
        }
    }

    @Test
    fun `repository error emits Error with message (movie)`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } throws RuntimeException("Not found")
        coEvery { tvShowRepository.getTvShowDetails(42) } returns testTvShwDetail

        val viewModel = MediaDetailsViewModel(
            savedStateHandle = createSavedStateHandle(42, mediaType = MediaType.MOVIE),
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as MediaDetailsUiState.Error
            assertEquals("Not found", error.message)
        }
    }

    @Test
    fun `repository error emits Error with message (tv show)`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } returns testMovieDetail
        coEvery { tvShowRepository.getTvShowDetails(42) } throws RuntimeException("Not found")

        val viewModel = MediaDetailsViewModel(
            savedStateHandle = createSavedStateHandle(42, mediaType = MediaType.TV_SHOW),
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as MediaDetailsUiState.Error
            assertEquals("Not found", error.message)
        }
    }

    @Test
    fun `repository error with null message emits Unknown error (movie)`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } throws RuntimeException()
        coEvery { tvShowRepository.getTvShowDetails(42) } returns testTvShwDetail

        val viewModel = MediaDetailsViewModel(
            savedStateHandle = createSavedStateHandle(mediaId = 42, mediaType = MediaType.MOVIE),
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as MediaDetailsUiState.Error
            assertEquals("Unknown error", error.message)
        }
    }

    @Test
    fun `repository error with null message emits Unknown error (tv show)`() = runTest {
        coEvery { movieRepository.getMovieDetails(42) } returns testMovieDetail
        coEvery { tvShowRepository.getTvShowDetails(42) } throws RuntimeException()

        val viewModel = MediaDetailsViewModel(
            savedStateHandle = createSavedStateHandle(mediaId = 42, mediaType = MediaType.TV_SHOW),
            movieRepository = movieRepository,
            tvShowRepository = tvShowRepository
        )

        viewModel.uiState.test {
            val error = expectMostRecentItem() as MediaDetailsUiState.Error
            assertEquals("Unknown error", error.message)
        }
    }
}
