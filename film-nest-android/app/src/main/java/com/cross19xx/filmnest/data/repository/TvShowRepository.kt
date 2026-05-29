package com.cross19xx.filmnest.data.repository

import com.cross19xx.filmnest.BuildConfig
import com.cross19xx.filmnest.data.model.Genre
import com.cross19xx.filmnest.data.model.MediaItem
import com.cross19xx.filmnest.data.model.MediaItemDetail
import com.cross19xx.filmnest.data.model.MediaType
import com.cross19xx.filmnest.data.remote.TmdbApiService
import javax.inject.Inject

class TvShowRepository @Inject constructor(private val api: TmdbApiService) {
    private companion object {
        const val IMAGE_BASE_URL = BuildConfig.TMDB_BASE_IMAGE_URL
    }

    suspend fun getPopularTvShows(page: Int = 1): List<MediaItem> {
        return api.getPopularTvShows(page).results.map { it.toMediaItem() }
    }

    suspend fun getTopRatedTvShows(page: Int = 1): List<MediaItem> {
        return api.getTopRatedTvShows(page).results.map { it.toMediaItem() }
    }

    suspend fun getTvShowsByGenre(genreId: Int, page: Int = 1): List<MediaItem> {
        return api.getTvShowsByGenre(genreId, page).results.map { it.toMediaItem() }
    }

    suspend fun getTvShowDetails(showId: Int): MediaItemDetail {
        val dto = api.getTvShowDetails(showId)
        return MediaItemDetail(
            id = dto.id,
            title = dto.name,
            originalTitle = dto.name,
            overview = dto.overview,
            posterPath = dto.posterPath?.let { "$IMAGE_BASE_URL$it" },
            backdropPath = dto.backdropPath?.let { "$IMAGE_BASE_URL$it" },
            genres = dto.genres.map { Genre(id = it.id, name = it.name) },
            homepage = null,
            releaseDate = dto.firstAirDate,
            runtimeMinutes = dto.episodeRunTime.firstOrNull(),
            status = dto.status,
            tagline = dto.tagline,
            voteAverage = dto.voteAverage,
            voteCount = dto.voteCount,
            popularity = dto.popularity,
            mediaType = MediaType.TV_SHOW,

            numberOfSeasons = dto.numberOfSeasons,
            numberOfEpisodes = dto.numberOfEpisodes,
            lastAirDate = dto.lastAirDate
        )
    }

    /**
     * `toMediaItem` extension
     * This method was not placed inside the `TvShowDto` because it would break the separation
     * between layers.
     *
     * `TvShowDto` lives inside the `data.remote.dto` — whose only job is to represent the TMDB JSON shape.
     * If we were to put `toMediaItem` inside it, the `TvShowDto` needs to import and know about `MediaItem`
     * from `data.model` (which is supposed to be local).
     *
     * This causes the API layer to depend on the domain layer.
     * There is a similar private extension in the MovieRepository.
     */
    private fun com.cross19xx.filmnest.data.remote.dto.TvShowDto.toMediaItem() = MediaItem(
        id = id,
        title = name,
        overview = overview,
        posterPath = posterPath?.let { "$IMAGE_BASE_URL$it" },
        backdropPath = backdropPath?.let { "$IMAGE_BASE_URL$it" },
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        genreIds = genreIds,
        popularity = popularity,
        mediaType = MediaType.TV_SHOW,
    )
}