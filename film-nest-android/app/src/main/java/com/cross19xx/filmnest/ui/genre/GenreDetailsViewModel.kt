package com.cross19xx.filmnest.ui.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cross19xx.filmnest.data.repository.GenreRepository
import com.cross19xx.filmnest.data.repository.MovieRepository
import com.cross19xx.filmnest.navigation.GenreDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val genreRepository: GenreRepository,
) : ViewModel() {

    private val genreId: Int = savedStateHandle.toRoute<GenreDetails>().genreId

    private val _uiState = MutableStateFlow<GenreDetailsUiState>(GenreDetailsUiState.Loading)
    val uiState: StateFlow<GenreDetailsUiState> = _uiState.asStateFlow()

    init {
        loadMoviesByGenre()
    }

    private fun loadMoviesByGenre() {
        viewModelScope.launch {
            try {
                coroutineScope {
                    val movies = async { movieRepository.getMoviesByGenre(genreId) }
                    val genre = async { genreRepository.getGenre(genreId) }

                    _uiState.value = GenreDetailsUiState.Success(
                        movies = movies.await(),
                        genre = genre.await()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = GenreDetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

}
