
package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.NetworkResponseModel
import com.mustafa.movieguideapp.models.entity.Tv

data class DiscoverTvResponse(
  val page: Int,
  val results: List<Tv>,
  val total_results: Int,
  val total_pages: Int
) : NetworkResponseModel
