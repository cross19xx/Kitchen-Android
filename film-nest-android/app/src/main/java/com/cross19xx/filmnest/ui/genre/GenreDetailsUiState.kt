package com.cross19xx.filmnest.ui.genre

import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MediaItem

sealed interface GenreDetailsUiState {

    data object Loading : GenreDetailsUiState

    data class Success(
        val mediaItems: List<MediaItem>,
        val genre: Genre,
    ) : GenreDetailsUiState

    data class Error(val message: String) : GenreDetailsUiState
}
