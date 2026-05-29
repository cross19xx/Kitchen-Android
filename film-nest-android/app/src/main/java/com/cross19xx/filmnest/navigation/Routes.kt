package com.cross19xx.filmnest.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
data class MovieDetails(val movieId: Int)

@Serializable
object Settings


@Serializable
data class GenreDetails(val genreId: Int)