
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.KeywordListResponse

class KeywordPagingChecker : NetworkPagingChecker<KeywordListResponse> {
  override fun hasNextPage(response: KeywordListResponse): Boolean {
    return false
  }
}
