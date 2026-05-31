package com.cross19xx.filmnest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchResultDto(
    val id: Int,

    // Can be "movie", "tv", or "person". Using a string to prevent issues and allow for
    // expansion in the future.
    @SerializedName("media_type") val mediaType: String,

    // Movie uses `title`; TV and Person use `name`
    val title: String?,
    val name: String?,

    val overview: String?,

    @SerializedName("poster_path") val posterPath: String?,      // movie + tv
    @SerializedName("backdrop_path") val backdropPath: String?,  // movie + tv
    @SerializedName("profile_path") val profilePath: String?,    // person

    // Movie uses `release_date`; TV uses `first_air_date`
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,

    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("vote_count") val voteCount: Int?,
    @SerializedName("genre_ids") val genreIds: List<Int>?,
)
