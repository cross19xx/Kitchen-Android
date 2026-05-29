package com.cross19xx.filmnest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TvShowListResponse(
    val page: Int,
    val results: List<TvShowDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)
