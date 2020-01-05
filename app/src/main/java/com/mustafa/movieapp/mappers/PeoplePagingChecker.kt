
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.PeopleResponse
import timber.log.Timber

class PeoplePagingChecker : NetworkPagingChecker<PeopleResponse> {
  override fun hasNextPage(response: PeopleResponse): Boolean {
    return response.page < response.total_pages
  }
}
