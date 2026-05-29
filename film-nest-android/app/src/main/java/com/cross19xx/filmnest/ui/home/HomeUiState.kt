package com.cross19xx.filmnest.ui.home

import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MediaItem

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val popular: List<MediaItem>,
        val nowPlaying: List<MediaItem>,
        val topRated: List<MediaItem>,
        val upcoming: List<MediaItem>,
        val genres: List<Genre>
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
