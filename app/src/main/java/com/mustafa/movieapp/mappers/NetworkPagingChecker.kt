
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.NetworkResponseModel

interface NetworkPagingChecker<in FROM : NetworkResponseModel> {
  fun hasNextPage(response: FROM): Boolean
}
