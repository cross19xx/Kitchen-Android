package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.BuildConfig
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.Movie
import com.cross19xx.filmnest.data.model.MovieDetail
import com.cross19xx.filmnest.data.remote.TmdbApiService
import javax.inject.Inject

class MovieRepository @Inject constructor(private val api: TmdbApiService) {

    private companion object {
        const val IMAGE_BASE_URL = BuildConfig.TMDB_BASE_IMAGE_URL
    }

    suspend fun getPopularMovies(page: Int = 1): List<Movie> {
        return api.getPopularMovies(page).results.map { it.toMovie() }
    }

    suspend fun getNowPlayingMovies(page: Int = 1): List<Movie> {
        return api.getNowPlayingMovies(page).results.map { it.toMovie() }
    }

    suspend fun getTopRatedMovies(page: Int = 1): List<Movie> {
        return api.getTopRatedMovies(page).results.map { it.toMovie() }
    }

    suspend fun getUpcomingMovies(page: Int = 1): List<Movie> {
        return api.getUpcomingMovies(page).results.map { it.toMovie() }
    }

    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): List<Movie> {
        return api.getMoviesByGenre(genreId, page).results.map { it.toMovie() }
    }

    suspend fun getMovieDetails(movieId: Int): MovieDetail {
        val dto = api.getMovieDetails(movieId)
        return MovieDetail(
            id = dto.id,
            title = dto.title,
            overview = dto.overview,
            tagline = dto.tagline,
            posterUrl = dto.posterPath?.let { "$IMAGE_BASE_URL$it" },
            backdropUrl = dto.backdropPath?.let { "$IMAGE_BASE_URL$it" },
            releaseDate = dto.releaseDate,
            voteAverage = dto.voteAverage,
            runtime = dto.runtime,
            budget = dto.budget,
            revenue = dto.revenue,
            status = dto.status,
            genres = dto.genres.map { Genre(id = it.id, name = it.name) },
        )
    }

    /**
     * `toMovie` extension
     * This method was not placed inside the `MovieDto` because it would break the separation
     * between layers.
     *
     * `MovieDto` lives inside the `data.remote.dto` — who's only job is to represent the TMDB JSON shape.
     * If we were to put the `toMovie` inside it, the `MovieDto` needs to import and know about the `Movie`
     * from `data.model` (which is supposed to be local).
     * This causes the API layer to depend on the domain layer.
     *
     * By keeping it as a private extension, the repository owns the mapping.
     */
    private fun com.cross19xx.filmnest.data.remote.dto.MovieDto.toMovie() = Movie(
        id = id,
        title = title,
        overview = overview,
        posterUrl = posterPath?.let { "$IMAGE_BASE_URL$it" },
        backdropUrl = backdropPath?.let { "$IMAGE_BASE_URL$it" },
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        genreIds = genreIds,
    )
}
