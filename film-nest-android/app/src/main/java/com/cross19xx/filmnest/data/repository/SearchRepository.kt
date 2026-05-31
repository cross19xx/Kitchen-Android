package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.BuildConfig
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.model.SearchResult
import com.cross19xx.filmnest.data.remote.TmdbApiService
import com.cross19xx.filmnest.data.remote.dto.SearchResultDto
import javax.inject.Inject

class SearchRepository @Inject constructor(private val api: TmdbApiService) {

    private companion object {
        const val IMAGE_BASE_URL = BuildConfig.TMDB_BASE_IMAGE_URL
    }

    suspend fun searchMulti(query: String, page: Int = 1): List<SearchResult> {
        return api.searchMulti(query, page).results.mapNotNull { it.toSearchResult() }
    }

    /**
     * Maps a [SearchResultDto] to a domain [SearchResult] based on its `media_type`.
     *
     * `search/multi` returns a mixed array of movies, TV shows, and people, so most DTO fields
     * are nullable. This mapping resolves those nulls and discriminates on `mediaType`:
     * "movie"/"tv" become a [SearchResult.Media], "person" becomes a [SearchResult.Person], and
     * any unknown type returns null so it can be dropped by `mapNotNull`.
     *
     * Like the other repositories, this extension lives here (not on the DTO) so the API layer
     * never has to depend on the domain layer.
     */
    private fun SearchResultDto.toSearchResult(): SearchResult? = when (mediaType) {
        "movie", "tv" -> SearchResult.Media(
            MediaItem(
                id = id,
                title = title ?: name.orEmpty(),
                overview = overview.orEmpty(),
                posterPath = posterPath?.let { "$IMAGE_BASE_URL$it" },
                backdropPath = backdropPath?.let { "$IMAGE_BASE_URL$it" },
                releaseDate = releaseDate ?: firstAirDate,
                voteAverage = voteAverage ?: 0.0,
                voteCount = voteCount ?: 0,
                genreIds = genreIds.orEmpty(),
                popularity = 0.0,
                mediaType = if (mediaType == "movie") MediaType.MOVIE else MediaType.TV_SHOW,
            )
        )

        "person" -> SearchResult.Person(
            id = id,
            name = name.orEmpty(),
            profilePath = profilePath?.let { "$IMAGE_BASE_URL$it" },
        )

        else -> null
    }
}
