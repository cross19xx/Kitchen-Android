package com.cross19xx.filmnest.ui.details

import com.cross19xx.filmnest.data.model.MediaItemDetail

sealed interface MediaDetailsUiState {
    data object Loading : MediaDetailsUiState

    data class Success(val media: MediaItemDetail) : MediaDetailsUiState

    data class Error(val message: String) : MediaDetailsUiState
}
