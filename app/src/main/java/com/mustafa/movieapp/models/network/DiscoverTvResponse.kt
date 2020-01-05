
package com.mustafa.movieapp.models.network

import com.mustafa.movieapp.models.NetworkResponseModel
import com.mustafa.movieapp.models.entity.Tv

data class DiscoverTvResponse(
  val page: Int,
  val results: List<Tv>,
  val total_results: Int,
  val total_pages: Int
) : NetworkResponseModel
