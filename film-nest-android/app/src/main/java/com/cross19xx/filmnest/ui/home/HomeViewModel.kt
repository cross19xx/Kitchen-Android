package com.cross19xx.filmnest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.Movie
import com.cross19xx.filmnest.data.repository.GenreRepository
import com.cross19xx.filmnest.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                val popular = async { movieRepository.getPopularMovies() }
                val nowPlaying = async { movieRepository.getNowPlayingMovies() }
                val topRated = async { movieRepository.getTopRatedMovies() }
                val upcoming = async { movieRepository.getUpcomingMovies() }

                val genres = async { genreRepository.getGenres() }

                _uiState.value = HomeUiState.Success(
                    popular = popular.await(),
                    nowPlaying = nowPlaying.await(),
                    topRated = topRated.await(),
                    upcoming = upcoming.await(),
                    genres = genres.await()
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
