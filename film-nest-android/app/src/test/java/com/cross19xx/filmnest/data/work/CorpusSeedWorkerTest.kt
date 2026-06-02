package com.cross19xx.filmnest.data.work

import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.repository.EmbeddingRepository
import com.cross19xx.filmnest.data.repository.MovieRepository
import com.cross19xx.filmnest.data.repository.TvShowRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CorpusSeedWorkerTest {
    private fun item(id: Int) = MediaItem(
        id = id, genreIds = emptyList(), mediaType = MediaType.MOVIE, overview = "o",
        popularity = 0.0, title = "t$id", voteAverage = 0.0, voteCount = 0,
        backdropPath = null, posterPath = null, releaseDate = null,
    )

    private fun buildWorker(
        movieRepository: MovieRepository,
        tvShowRepository: TvShowRepository,
        embeddingRepository: EmbeddingRepository,
    ): CorpusSeedWorker {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        return TestListenableWorkerBuilder<CorpusSeedWorker>(context)
            .setWorkerFactory(object : WorkerFactory() {
                override fun createWorker(
                    appContext: android.content.Context,
                    workerClassName: String,
                    workerParameters: WorkerParameters,
                ) = CorpusSeedWorker(
                    appContext, workerParameters,
                    movieRepository, tvShowRepository, embeddingRepository,
                )
            })
            .build()
    }

    @Test
    fun `doWork fetches popular movies and shows and embeds each item`() = runBlocking {
        val movieRepository = mockk<MovieRepository>()
        val tvShowRepository = mockk<TvShowRepository>()
        val embeddingRepository = mockk<EmbeddingRepository>()
        coEvery { movieRepository.getPopularMovies(any()) } returns listOf(item(1))
        coEvery { tvShowRepository.getPopularTvShows(any()) } returns listOf(item(2))
        coEvery { embeddingRepository.embedIfAbsent(any()) } just Runs

        val result = buildWorker(movieRepository, tvShowRepository, embeddingRepository).doWork()

        assertEquals(ListenableWorker.Result.success(), result)
        coVerify { embeddingRepository.embedIfAbsent(item(1)) }
        coVerify { embeddingRepository.embedIfAbsent(item(2)) }
    }

    @Test
    fun `doWork retries when fetching fails`() = runBlocking {
        val movieRepository = mockk<MovieRepository>()
        val tvShowRepository = mockk<TvShowRepository>()
        val embeddingRepository = mockk<EmbeddingRepository>()
        coEvery { movieRepository.getPopularMovies(any()) } throws RuntimeException("network")
        coEvery { tvShowRepository.getPopularTvShows(any()) } returns emptyList()

        val result = buildWorker(movieRepository, tvShowRepository, embeddingRepository).doWork()

        assertEquals(ListenableWorker.Result.retry(), result)
    }
}
