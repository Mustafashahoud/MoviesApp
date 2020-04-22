
package com.mustafa.movieapp.api

import androidx.lifecycle.LiveData
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.network.KeywordListResponse
import com.mustafa.movieapp.models.network.ReviewListResponse
import com.mustafa.movieapp.models.network.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface MovieService {

  @GET("/3/movie/{movie_id}/keywords")
  fun fetchKeywords(@Path("movie_id") id: Int): LiveData<ApiResponse<KeywordListResponse>>

  @GET("/3/movie/{movie_id}/videos")
  fun fetchVideos(@Path("movie_id") id: Int): LiveData<ApiResponse<VideoListResponse>>

  @GET("/3/movie/{movie_id}/reviews")
  fun fetchReviews(@Path("movie_id") id: Int): LiveData<ApiResponse<ReviewListResponse>>

}
