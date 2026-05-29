package com.cross19xx.filmnest.ui.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.repository.MovieRepository
import com.cross19xx.filmnest.data.repository.TvShowRepository
import com.cross19xx.filmnest.navigation.MediaDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MediaDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
) : ViewModel() {
    /*
     * Use SavedStateHandle to fetch the ID from the route because to decouple the view model from
     * the view (sort of) and to ensure that we're not passing the ID to the view model, something
     * that might break Hilt's DI graph
     */
    private val args = savedStateHandle.toRoute<MediaDetails>()
    private val mediaId = args.mediaId
    private val mediaType = args.mediaType

    private val _uiState = MutableStateFlow<MediaDetailsUiState>(MediaDetailsUiState.Loading)
    val uiState: StateFlow<MediaDetailsUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetails()
    }

    private fun loadMovieDetails() {
        println("$mediaId and media type $mediaType")

        viewModelScope.launch {
            try {
                val media = if (mediaType == MediaType.MOVIE) {
                    movieRepository.getMovieDetails(mediaId)
                } else {
                    tvShowRepository.getTvShowDetails(mediaId)
                }

                _uiState.value = MediaDetailsUiState.Success(media)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MediaDetailsUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
