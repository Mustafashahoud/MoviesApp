
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.DiscoverTvResponse

class TvPagingChecker : NetworkPagingChecker<DiscoverTvResponse> {
  override fun hasNextPage(response: DiscoverTvResponse): Boolean {
    return response.page < response.total_pages
  }
}
