package com.cross19xx.filmnest.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cross19xx.filmnest.data.repository.GenreRepository
import com.cross19xx.filmnest.data.repository.MovieRepository
import com.cross19xx.filmnest.data.repository.TvShowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val genreRepository: GenreRepository,
    private val movieRepository: MovieRepository,
    private val tvShowRepository: TvShowRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    private fun loadMovies() {
        viewModelScope.launch {
            try {
                supervisorScope {
                    val popularMovies = async { movieRepository.getPopularMovies() }
                    val nowPlayingMovies = async { movieRepository.getNowPlayingMovies() }
                    val topRatedMovies = async { movieRepository.getTopRatedMovies() }
                    val upcomingMovies = async { movieRepository.getUpcomingMovies() }

                    val popularTvShows = async { tvShowRepository.getPopularTvShows() }
                    val topRatedTvShows = async { tvShowRepository.getTopRatedTvShows() }

                    val genres = async { genreRepository.getGenres() }

                    _uiState.value = HomeUiState.Success(
                        popular = (popularMovies.await() + popularTvShows.await()).sortedBy { it.title },
                        nowPlaying = nowPlayingMovies.await().sortedBy { it.title },
                        topRated = (topRatedMovies.await() + topRatedTvShows.await()).sortedBy { it.title },
                        upcoming = (upcomingMovies.await()).sortedBy { it.title },
                        genres = genres.await()
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
