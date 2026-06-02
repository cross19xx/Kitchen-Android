package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Embeds browsed items into the local corpus, lazily and off the main thread.
 *
 * Fire-and-forget: callers `enqueue` items they already fetched; a single consumer
 * coroutine drains the queue and embeds anything not already cached.
 */
@Singleton
class CorpusUpdater @Inject constructor(
    @ApplicationScope scope: CoroutineScope,
    private val embeddingRepository: EmbeddingRepository
) {
    private val queue = Channel<MediaItem>(Channel.UNLIMITED)

    init {
        scope.launch {
            for (item in queue) {
                runCatching { embeddingRepository.embedIfAbsent(item) }
            }
        }
    }

    fun enqueue(items: List<MediaItem>) {
        items.forEach { queue.trySend(it) }
    }
}
