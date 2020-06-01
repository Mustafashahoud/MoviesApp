
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.MoviePersonResponse

class MoviePersonPagingChecker : NetworkPagingChecker<MoviePersonResponse> {
  override fun hasNextPage(response: MoviePersonResponse): Boolean {
    return false
  }
}
