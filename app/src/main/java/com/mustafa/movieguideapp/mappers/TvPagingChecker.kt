
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.DiscoverTvResponse

class TvPagingChecker : NetworkPagingChecker<DiscoverTvResponse> {
  override fun hasNextPage(response: DiscoverTvResponse): Boolean {
    return response.page < response.total_pages
  }
}
