
package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.NetworkResponseModel
import com.mustafa.movieguideapp.models.Review

class ReviewListResponse(
  val id: Int,
  val page: Int,
  val results: List<Review>,
  val total_pages: Int,
  val total_results: Int
) : NetworkResponseModel
