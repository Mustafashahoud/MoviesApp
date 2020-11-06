
package com.mustafa.movieguideapp.api

import com.mustafa.movieguideapp.models.network.KeywordListResponse
import com.mustafa.movieguideapp.models.network.ReviewListResponse
import com.mustafa.movieguideapp.models.network.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface TvService {


  @GET("/3/tv/{tv_id}/keywords")
  suspend fun fetchKeywords(@Path("tv_id") id: Int): ApiResponse<KeywordListResponse>

  @GET("/3/tv/{tv_id}/videos")
  suspend fun fetchVideos(@Path("tv_id") id: Int): ApiResponse<VideoListResponse>

  @GET("/3/tv/{tv_id}/reviews")
  suspend fun fetchReviews(@Path("tv_id") id: Int): ApiResponse<ReviewListResponse>
}
