package com.cross19xx.filmnest.data.model

sealed interface SearchResult {
    data class Media(val item: MediaItem) : SearchResult

    data class Person(
        val id: Int,
        val name: String,
        val profilePath: String?
    ) : SearchResult
}
