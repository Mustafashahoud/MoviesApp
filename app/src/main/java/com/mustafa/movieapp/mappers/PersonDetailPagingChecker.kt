
package com.mustafa.movieapp.mappers

import com.mustafa.movieapp.models.network.PersonDetail

class PersonDetailPagingChecker : NetworkPagingChecker<PersonDetail> {
  override fun hasNextPage(response: PersonDetail): Boolean {
    return false
  }
}
