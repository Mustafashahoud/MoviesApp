
package com.mustafa.movieapp.models

@Suppress("unused")
data class ResponseModel(
  val page: Int,
  val results: Any,
  val total_results: Int,
  val total_pages: Int
)
