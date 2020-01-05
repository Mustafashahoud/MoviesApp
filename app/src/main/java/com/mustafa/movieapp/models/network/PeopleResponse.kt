
package com.mustafa.movieapp.models.network

import com.mustafa.movieapp.models.NetworkResponseModel
import com.mustafa.movieapp.models.entity.Person

data class PeopleResponse(
  val page: Int,
  val results: List<Person>,
  val total_results: Int,
  val total_pages: Int
) : NetworkResponseModel
