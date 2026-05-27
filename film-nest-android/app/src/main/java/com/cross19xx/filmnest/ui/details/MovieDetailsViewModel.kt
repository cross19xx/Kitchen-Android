package com.cross19xx.filmnest.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cross19xx.filmnest.navigation.MovieDetails
import com.cross19xx.filmnest.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
) : ViewModel() {
    /*
     * Use SavedStateHandle to fetch the ID from the route because to decouple the view model from
     * the view (sort of) and to ensure that we're not passing the ID to the view model, something
     * that might break Hilt's DI graph
     */
    private val movieId = savedStateHandle.toRoute<MovieDetails>().movieId

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        viewModelScope.launch {

            try {
                // No need for async because I'm running one method in the description
                val movie = movieRepository.getMovieDetails(movieId)
                _uiState.value = MovieDetailsUiState.Success(movie)
            } catch (e: Exception) {
                _uiState.value = MovieDetailsUiState.Error(e.message ?: "Unknown error")
            }

        }
    }
}
