package com.cross19xx.filmnest.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.cross19xx.filmnest.data.repository.EmbeddingRepository
import com.cross19xx.filmnest.data.repository.MovieRepository
import com.cross19xx.filmnest.data.repository.TvShowRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CorpusSeedWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
    private val embeddingRepository: EmbeddingRepository,
) : CoroutineWorker(appContext, params) {

    companion object {
        const val UNIQUE_NAME = "corpus_seed"
        private const val PAGES = 10
    }

    override suspend fun doWork(): Result = try {
        val items = buildList {
            for (page in 1..PAGES) {
                addAll(movieRepository.getPopularMovies(page))
                addAll(tvShowRepository.getPopularTvShows(page))
            }
        }

        items.forEach { embeddingRepository.embedIfAbsent(it) }
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}