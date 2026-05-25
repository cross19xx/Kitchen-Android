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
}
