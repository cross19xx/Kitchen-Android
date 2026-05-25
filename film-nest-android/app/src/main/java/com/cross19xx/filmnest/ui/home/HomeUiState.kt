package com.cross19xx.filmnest.ui.home

import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.Movie

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val popular: List<Movie>,
        val nowPlaying: List<Movie>,
        val topRated: List<Movie>,
        val upcoming: List<Movie>,
        val genres: List<Genre>
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
