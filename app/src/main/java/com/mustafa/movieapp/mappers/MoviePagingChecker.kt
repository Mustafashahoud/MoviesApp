
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.DiscoverMovieResponse

class MoviePagingChecker : NetworkPagingChecker<DiscoverMovieResponse> {
  override fun hasNextPage(response: DiscoverMovieResponse): Boolean {
    return response.page < response.total_pages
  }
}
