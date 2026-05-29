package com.cross19xx.filmnest.data.model

data class MediaItem(
    val id: Int,

    val genreIds: List<Int>,
    val mediaType: MediaType,
    val overview: String,
    val popularity: Double,
    val title: String,
    val voteAverage: Double,
    val voteCount: Int,

    val backdropPath: String?,
    val posterPath: String?,
    val releaseDate: String?,
)
