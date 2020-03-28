
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.MoviePersonResponse

class MoviePersonPagingChecker : NetworkPagingChecker<MoviePersonResponse> {
  override fun hasNextPage(response: MoviePersonResponse): Boolean {
    return false
  }
}
