
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.DiscoverMovieResponse

class MoviePagingChecker : NetworkPagingChecker<DiscoverMovieResponse> {
  override fun hasNextPage(response: DiscoverMovieResponse): Boolean {
    return response.page < response.total_pages
  }
}
