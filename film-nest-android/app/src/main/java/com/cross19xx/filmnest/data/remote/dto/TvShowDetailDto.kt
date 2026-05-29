package com.cross19xx.filmnest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TvShowDetailDto(
    val id: Int,
    val name: String,
    val overview: String,
    val tagline: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("episode_run_time") val episodeRunTime: List<Int>,
    @SerializedName("number_of_episodes") val numberOfEpisodes: Int,
    @SerializedName("number_of_seasons") val numberOfSeasons: Int,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("last_air_date") val lastAirDate: String?,
    val popularity: Double,
    val status: String,
    val genres: List<GenreDto>,
)