
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.ReviewListResponse

class ReviewPagingChecker : NetworkPagingChecker<ReviewListResponse> {
  override fun hasNextPage(response: ReviewListResponse): Boolean {
    return false
  }
}
