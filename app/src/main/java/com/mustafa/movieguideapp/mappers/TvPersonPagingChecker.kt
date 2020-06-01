
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.TvPersonResponse

class TvPersonPagingChecker : NetworkPagingChecker<TvPersonResponse> {
  override fun hasNextPage(response: TvPersonResponse): Boolean {
    return false
  }
}
