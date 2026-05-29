package com.cross19xx.filmnest.data.model

import kotlinx.serialization.Serializable

// Serializable is required by Compose Navigation, even though it's a simple enum class
@Serializable
enum class MediaType {
    MOVIE,
    TV_SHOW
}
