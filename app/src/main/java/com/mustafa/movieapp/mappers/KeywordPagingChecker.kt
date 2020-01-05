
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.KeywordListResponse

class KeywordPagingChecker : NetworkPagingChecker<KeywordListResponse> {
  override fun hasNextPage(response: KeywordListResponse): Boolean {
    return false
  }
}
