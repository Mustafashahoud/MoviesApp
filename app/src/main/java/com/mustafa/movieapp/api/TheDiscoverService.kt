
package com.mustafa.movieapp.api

import androidx.lifecycle.LiveData
import com.mustafa.movieapp.models.network.DiscoverMovieResponse
import com.mustafa.movieapp.models.network.DiscoverTvResponse
import com.mustafa.movieapp.models.network.PeopleResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TheDiscoverService {
  /**
   * [Movie Discover](https://developers.themoviedb.org/3/discover/movie-discover)
   *
   *  Discover movies by different types of data like average rating, number of votes, genres and certifications.
   *  You can get a valid list of certifications from the  method.
   *
   *  @param [page] Specify the page of results to query.
   *
   *  @return [DiscoverMovieResponse] response
   */
  @GET("/3/discover/movie?language=en&sort_by=popularity.desc")
  fun fetchDiscoverMovie(@Query("page") page: Int): LiveData<ApiResponse<DiscoverMovieResponse>>

  /**
   * [Tv Discover](https://developers.themoviedb.org/3/discover/tv-discover)
   *
   *  Discover TV shows by different types of data like average rating, number of votes, genres, the network they aired on and air dates.
   *
   *  @param [page] Specify the page of results to query.
   *
   *  @return [DiscoverTvResponse] response
   */
  @GET("/3/discover/tv?language=en&sort_by=popularity.desc")
  fun fetchDiscoverTv(@Query("page") page: Int): LiveData<ApiResponse<DiscoverTvResponse>>

  /**
   *
   * @param [query] name of a movie to be searched for.
   * @param [page] Specify the page of results to query.
   * @return [DiscoverMovieResponse] response
   */
  @GET("/3/search/movie")
  fun searchMovies(@Query("query") query: String,
                   @Query("page") page: Int) : LiveData<ApiResponse<DiscoverMovieResponse>>


    /**
     *
     * @param [query] name of a tv to be searched for.
     * @param [page] Specify the page of results to query.
     * @return [DiscoverTvResponse] response
     */
    @GET("/3/search/tv")
    fun searchTvs(@Query("query") query: String,
                     @Query("page") page: Int) : LiveData<ApiResponse<DiscoverTvResponse>>


  /**
   * @param [query] a name of a person to be searched for.
   * @param [page] Specify the page of results to query.
   * @return [PeopleResponse] response
   */
  @GET("/3/discover/movie")
  fun searchMovieFilters(
                    @Query("vote_average.gte") rating: Int?,
                    @Query("sort_by") sort: String?,
                    @Query("year") year: Int?,
                    @Query("with_genres") with_genres: String?,
                    @Query("with_keywords") with_keywords: String?,
                    @Query("with_original_language") with_original_language: String?,
                    @Query("with_runtime.gte") with_runtime: Int?,
                    @Query("region") region: String?,
                    @Query("page") page: Int)
          : LiveData<ApiResponse<DiscoverMovieResponse>>

    /**
     * @param [query] a name of a person to be searched for.
     * @param [page] Specify the page of results to query.
     * @return [PeopleResponse] response
     */
    @GET("/3/discover/tv")
    fun searchTvFilters(
        @Query("vote_average.gte") rating: Int?,
        @Query("sort_by") sort: String?,
        @Query("first_air_date_year") year: Int?,
        @Query("with_genres") with_genres: String?,
        @Query("with_keywords") with_keywords: String?,
        @Query("with_original_language") with_original_language: String?,
        @Query("with_runtime.gte") with_runtime: Int?,
        @Query("page") page: Int)
            : LiveData<ApiResponse<DiscoverTvResponse>>
}




