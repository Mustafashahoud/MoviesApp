
package com.mustafa.movieapp.models.network

import com.mustafa.movieapp.models.NetworkResponseModel
import com.mustafa.movieapp.models.entity.Movie

data class DiscoverMovieResponse(
  val page: Int,
  val results: List<Movie>,
  val total_results: Int,
  val total_pages: Int
) : NetworkResponseModel
