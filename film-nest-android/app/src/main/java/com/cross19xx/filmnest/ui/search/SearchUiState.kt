package com.cross19xx.filmnest.ui.search

import com.cross19xx.filmnest.data.model.SearchResult

sealed interface SearchUiState {
    data object Idle : SearchUiState // Initial state — prompt the user
    data object Loading : SearchUiState // Search in progress
    data object Empty : SearchUiState // Query ran with no matches

    data class Success(val results: List<SearchResult>) : SearchUiState
    data class Error(val message: String) : SearchUiState
}
