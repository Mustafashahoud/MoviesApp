
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
   * @param [query] a ame of a movie to be searched for.
   * @param [page] Specify the page of results to query.
   * @return [DiscoverMovieResponse] response
   */
  @GET("/3/search/movie")
  fun searchMovies(@Query("query") query: String,
                   @Query("page") page: Int) : LiveData<ApiResponse<DiscoverMovieResponse>>

  /**
   *
   * @param [query] a name of a tv to be searched for.
   * @param [page] Specify the page of results to query.
   * @return [DiscoverTvResponse] response
   */
  @GET("/3/search/tv")
  fun searchTVs(@Query("query") query: String,
                @Query("page") page: Int) : LiveData<ApiResponse<DiscoverTvResponse>>

  /**
   * @param [query] a name of a person to be searched for.
   * @param [page] Specify the page of results to query.
   * @return [PeopleResponse] response
   */
  @GET("/3/search/people")
  fun searchPeople(@Query("query") query: String,
                   @Query("page") page: Int) : LiveData<ApiResponse<PeopleResponse>>
}
