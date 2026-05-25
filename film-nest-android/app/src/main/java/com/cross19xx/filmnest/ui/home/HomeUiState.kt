package com.cross19xx.filmnest.ui.home

import com.cross19xx.filmnest.data.model.Movie

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(val movies: List<Movie>) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
