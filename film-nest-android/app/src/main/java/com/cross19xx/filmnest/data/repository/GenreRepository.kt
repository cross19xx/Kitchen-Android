package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.remote.TmdbApiService
import javax.inject.Inject

class GenreRepository @Inject constructor(private val api: TmdbApiService) {

    suspend fun getGenres(): List<Genre> {
        return api.getGenres().genres.map { dto ->
            Genre(id = dto.id, name = dto.name)
        }
    }

    suspend fun getGenre(genreId: Int): Genre {
        val genresDto = api.getGenres()
        val genreDto = genresDto.genres.find { it.id == genreId }

        if (genreDto == null) {
            return Genre(id = genreId, name = "Unknown")
        }
        return Genre(id = genreDto.id, name = genreDto.name)
    }
}
