package com.cross19xx.filmnest.data.remote

import com.cross19xx.filmnest.data.remote.dto.GenreListResponse
import com.cross19xx.filmnest.data.remote.dto.MovieDetailDto
import com.cross19xx.filmnest.data.remote.dto.MovieListResponse
import com.cross19xx.filmnest.data.remote.dto.TvShowDetailDto
import com.cross19xx.filmnest.data.remote.dto.TvShowListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreListResponse

    @GET("genre/tv/list")
    suspend fun getTvGenres(): GenreListResponse

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("tv/popular")
    suspend fun getPopularTvShows(@Query("page") page: Int = 1): TvShowListResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("tv/top_rated")
    suspend fun getTopRatedTvShows(@Query("page") page: Int = 1): TvShowListResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int = 1): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetailDto

    @GET("tv/{tv_show_id}")
    suspend fun getTvShowDetails(@Path("tv_show_id") showId: Int): TvShowDetailDto

    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
    ): MovieListResponse

    @GET("discover/tv")
    suspend fun getTvShowsByGenre(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1,
    ): TvShowListResponse
}
