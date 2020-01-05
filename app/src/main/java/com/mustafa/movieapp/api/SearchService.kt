package com.mustafa.movieapp.api

import androidx.lifecycle.LiveData
import com.mustafa.movieapp.models.network.DiscoverMovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    /**
     * Get the keywords that have been added to a movie.
     *
     * @param [query] a ame of a movie to be searched for.
     *
     * @return [DiscoverMovieResponse] response
     */
    @GET("/3/search/movie")
    fun searchMovies(@Query("query") query: String) : LiveData<ApiResponse<DiscoverMovieResponse>>
}