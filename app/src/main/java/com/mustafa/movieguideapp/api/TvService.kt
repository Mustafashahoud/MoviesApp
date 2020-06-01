
package com.mustafa.movieguideapp.api

import androidx.lifecycle.LiveData
import com.mustafa.movieguideapp.models.network.KeywordListResponse
import com.mustafa.movieguideapp.models.network.ReviewListResponse
import com.mustafa.movieguideapp.models.network.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TvService {

  @GET("/3/tv/{tv_id}/keywords")
  fun fetchKeywords(@Path("tv_id") id: Int): LiveData<ApiResponse<KeywordListResponse>>

  @GET("/3/tv/{tv_id}/videos")
  fun fetchVideos(@Path("tv_id") id: Int): LiveData<ApiResponse<VideoListResponse>>

  @GET("/3/tv/{tv_id}/reviews")
  fun fetchReviews(@Path("tv_id") id: Int): LiveData<ApiResponse<ReviewListResponse>>
}
