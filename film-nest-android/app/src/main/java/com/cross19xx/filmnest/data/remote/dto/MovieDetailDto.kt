package com.cross19xx.filmnest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDetailDto(
    val id: Int,
    val title: String,
    val overview: String,
    val tagline: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("imdb_id") val imdbId: String?,
    val runtime: Int?,
    val budget: Long,
    val revenue: Long,
    val status: String,
    val genres: List<GenreDto>,
    val popularity: Double,
)
