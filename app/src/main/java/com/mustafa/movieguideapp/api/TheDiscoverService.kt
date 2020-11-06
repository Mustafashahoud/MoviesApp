
package com.mustafa.movieguideapp.api

import com.mustafa.movieguideapp.models.network.DiscoverMovieResponse
import com.mustafa.movieguideapp.models.network.DiscoverTvResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TheDiscoverService {

    @GET("/3/discover/movie?language=en&sort_by=popularity.desc")
    suspend fun fetchMovies(@Query("page") page: Int): ApiResponse<DiscoverMovieResponse>

    @GET("/3/discover/tv?language=en&sort_by=popularity.desc")
    suspend fun fetchTvs(@Query("page") page: Int): ApiResponse<DiscoverTvResponse>

    @GET("/3/search/movie")
    suspend fun fetchSearchMovies(@Query("query") query: String,
                          @Query("page") page: Int) : ApiResponse<DiscoverMovieResponse>


    @GET("/3/search/tv")
    suspend fun fetchSearchTvs(@Query("query") query: String,
                          @Query("page") page: Int) : ApiResponse<DiscoverTvResponse>

    @GET("/3/discover/movie")
    suspend fun searchMovieFilters(
        @Query("vote_average.gte") rating: Int?,
        @Query("sort_by") sort: String?,
        @Query("year") year: Int?,
        @Query("with_genres") with_genres: String?,
        @Query("with_keywords") with_keywords: String?,
        @Query("with_original_language") with_original_language: String?,
        @Query("with_runtime.gte") with_runtime: Int?,
        @Query("region") region: String?,
        @Query("page") page: Int)
            : ApiResponse<DiscoverMovieResponse>


    @GET("/3/discover/tv")
    suspend fun searchTvFilters(
        @Query("vote_average.gte") rating: Int?,
        @Query("sort_by") sort: String?,
        @Query("first_air_date_year") year: Int?,
        @Query("with_genres") with_genres: String?,
        @Query("with_keywords") with_keywords: String?,
        @Query("with_original_language") with_original_language: String?,
        @Query("with_runtime.gte") with_runtime: Int?,
        @Query("page") page: Int)
            : ApiResponse<DiscoverTvResponse>


}




