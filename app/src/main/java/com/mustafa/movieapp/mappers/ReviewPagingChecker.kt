
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.ReviewListResponse

class ReviewPagingChecker : NetworkPagingChecker<ReviewListResponse> {
  override fun hasNextPage(response: ReviewListResponse): Boolean {
    return false
  }
}
