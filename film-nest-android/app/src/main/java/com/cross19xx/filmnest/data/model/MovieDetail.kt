package com.cross19xx.filmnest.data.model

data class MovieDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val tagline: String?,
    val posterUrl: String?,
    val backdropUrl: String?,
    val releaseDate: String?,
    val voteAverage: Double,
    val runtime: Int?,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val genres: List<Genre>,
)
