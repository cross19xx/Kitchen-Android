package com.cross19xx.filmnest.ui.details

import com.cross19xx.filmnest.data.model.MovieDetail

sealed interface MovieDetailsUiState {
    data object Loading : MovieDetailsUiState

    data class Success(val movie: MovieDetail) : MovieDetailsUiState

    data class Error(val message: String) : MovieDetailsUiState
}
