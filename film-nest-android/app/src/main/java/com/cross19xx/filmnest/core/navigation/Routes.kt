package com.cross19xx.filmnest.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class MovieDetails(val movieId: String)

@Serializable
object Settings
