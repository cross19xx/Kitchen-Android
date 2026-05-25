package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.core.network.RetrofitClient
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.remote.TmdbApiService

class GenreRepository {
    private val api = RetrofitClient.instance.create(TmdbApiService::class.java)

    suspend fun getGenres(): List<Genre> {
        return api.getGenres().genres.map { dto ->
            Genre(id = dto.id, name = dto.name)
        }
    }
}
