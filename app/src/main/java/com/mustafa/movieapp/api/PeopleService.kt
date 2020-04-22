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

    @GET("/3/person/popular?language=en")
    fun fetchPopularPeople(@Query("page") page: Int): LiveData<ApiResponse<PeopleResponse>>

    @GET("/3/person/{person_id}")
    fun fetchPersonDetail(@Path("person_id") id: Int): LiveData<ApiResponse<PersonDetail>>

    @GET("/3/person/{person_id}/movie_credits")
    fun fetchPersonMovies(@Path("person_id") id: Int): LiveData<ApiResponse<MoviePersonResponse>>

    @GET("/3/person/{person_id}/tv_credits")
    fun fetchPersonTvs(@Path("person_id") id: Int): LiveData<ApiResponse<TvPersonResponse>>

    @GET("/3/search/person")
    fun searchPeople(
        @Query("query") query: String,
        @Query("page") page: Int
    ): LiveData<ApiResponse<PeopleResponse>>

}
