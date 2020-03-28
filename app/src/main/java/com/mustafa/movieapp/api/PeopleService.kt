
package com.mustafa.movieapp.api

import androidx.lifecycle.LiveData
import com.mustafa.movieapp.models.network.MoviePersonResponse
import com.mustafa.movieapp.models.network.PeopleResponse
import com.mustafa.movieapp.models.network.PersonDetail
import com.mustafa.movieapp.models.network.TvPersonResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PeopleService {
  /**
   * [People Popular](https://developers.themoviedb.org/3/people/get-popular-people)
   *
   * Get the list of popular people on TMDb. This list updates daily.
   *
   * @param [page] Specify the page of results to query.
   *
   * @return [PeopleResponse] response
   */
  @GET("/3/person/popular?language=en")
  fun fetchPopularPeople(@Query("page") page: Int): LiveData<ApiResponse<PeopleResponse>>

  /**
   * [Person Detail](https://developers.themoviedb.org/3/people/get-person-details)
   *
   * Get the primary person details by id.
   *
   * @para [id] Specify the id of results to query.
   *
   * @return [PersonDetail] response
   */
  @GET("/3/person/{person_id}")
  fun fetchPersonDetail(@Path("person_id") id: Int): LiveData<ApiResponse<PersonDetail>>



  @GET("/3/person/{person_id}/movie_credits")
  fun fetchPersonMovies(@Path("person_id") id: Int): LiveData<ApiResponse<MoviePersonResponse>>

  @GET("/3/person/{person_id}/tv_credits")
  fun fetchPersonTvs(@Path("person_id") id: Int): LiveData<ApiResponse<TvPersonResponse>>




}
