
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.TvPersonResponse

class TvPersonPagingChecker : NetworkPagingChecker<TvPersonResponse> {
  override fun hasNextPage(response: TvPersonResponse): Boolean {
    return false
  }
}
