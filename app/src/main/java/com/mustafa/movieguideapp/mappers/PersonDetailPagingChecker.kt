
package com.mustafa.movieguideapp.mappers

import com.mustafa.movieguideapp.models.network.PersonDetail

class PersonDetailPagingChecker : NetworkPagingChecker<PersonDetail> {
  override fun hasNextPage(response: PersonDetail): Boolean {
    return false
  }
}
