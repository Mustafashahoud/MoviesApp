
package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.NetworkResponseModel
import com.mustafa.movieguideapp.models.entity.Person

data class PeopleResponse(
  val page: Int,
  val results: List<Person>,
  val total_results: Int,
  val total_pages: Int
) : NetworkResponseModel
