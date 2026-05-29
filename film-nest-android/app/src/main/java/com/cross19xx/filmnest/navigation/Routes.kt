package com.cross19xx.filmnest.navigation

import com.cross19xx.filmnest.data.model.MediaType
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class MediaDetails(val mediaId: Int, val mediaType: MediaType)

@Serializable
object Settings


@Serializable
data class GenreDetails(val genreId: Int)
