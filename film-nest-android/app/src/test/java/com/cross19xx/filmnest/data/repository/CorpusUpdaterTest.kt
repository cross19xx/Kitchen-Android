package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaType
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CorpusUpdaterTest {

    private fun item(id: Int) = MediaItem(
        id = id, genreIds = emptyList(), mediaType = MediaType.MOVIE, overview = "o",
        popularity = 0.0, title = "t$id", voteAverage = 0.0, voteCount = 0,
        backdropPath = null, posterPath = null, releaseDate = null,
    )

    @Test
    fun `enqueue drains items through embedIfAbsent on the provided scope`() = runTest {
        val repository = mockk<EmbeddingRepository>()
        coEvery { repository.embedIfAbsent(any()) } just Runs
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val updater = CorpusUpdater(scope, repository)

        updater.enqueue(listOf(item(1), item(2)))
        advanceUntilIdle()

        coVerify { repository.embedIfAbsent(item(1)) }
        coVerify { repository.embedIfAbsent(item(2)) }
    }

    @Test
    fun `a failing item does not stop later items from embedding`() = runTest {
        val repository = mockk<EmbeddingRepository>()
        coEvery { repository.embedIfAbsent(item(1)) } throws RuntimeException("boom")
        coEvery { repository.embedIfAbsent(item(2)) } just Runs
        val scope = TestScope(StandardTestDispatcher(testScheduler))
        val updater = CorpusUpdater(scope, repository)

        updater.enqueue(listOf(item(1), item(2)))
        advanceUntilIdle()

        coVerify { repository.embedIfAbsent(item(2)) }
    }
}
