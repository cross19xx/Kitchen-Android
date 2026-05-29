package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.remote.TmdbApiService
import javax.inject.Inject

class GenreRepository @Inject constructor(private val api: TmdbApiService) {

    suspend fun getGenres(): List<Genre> {
        val movieGenresDto = api.getMovieGenres()
        val tvShowGenresDto = api.getTvGenres()

        val genres = (movieGenresDto.genres + tvShowGenresDto.genres).map { dto ->
            Genre(id = dto.id, name = dto.name)
        }

        return genres.distinctBy { it.id }.sortedBy { it.name }
    }

    suspend fun getGenre(genreId: Int): Genre {
        val movieGenresDto = api.getMovieGenres()
        val tvShowGenresDto = api.getTvGenres()

        val genresDto = movieGenresDto.genres + tvShowGenresDto.genres
        val genreDto = genresDto.find { it.id == genreId }

        if (genreDto == null) {
            return Genre(id = genreId, name = "Unknown")
        }
        return Genre(id = genreDto.id, name = genreDto.name)
    }
}
