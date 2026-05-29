package com.cross19xx.filmnest.data.model

data class MediaItemDetail(
    val id: Int,

    val genres: List<Genre>,
    val mediaType: MediaType,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val status: String,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,

    val backdropPath: String?,
    val budget: Long? = null,
    val homepage: String?,
    val imdbId: String? = null,
    val lastAirDate: String? = null,
    val numberOfEpisodes: Int? = null,
    val numberOfSeasons: Int? = null,
    val posterPath: String?,
    val releaseDate: String?,
    val revenue: Long? = null,
    val runtimeMinutes: Int?,
    val tagline: String?,
)
