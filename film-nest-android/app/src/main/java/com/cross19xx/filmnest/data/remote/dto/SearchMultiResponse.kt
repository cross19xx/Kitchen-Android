package com.cross19xx.filmnest.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SearchMultiResponse(
    val page: Int,
    val results: List<SearchResultDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int,
)
