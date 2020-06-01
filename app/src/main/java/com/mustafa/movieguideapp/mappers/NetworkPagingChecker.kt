
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.NetworkResponseModel

interface NetworkPagingChecker<in FROM : NetworkResponseModel> {
  fun hasNextPage(response: FROM): Boolean
}
