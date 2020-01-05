
package com.mustafa.movieapp.models.network

import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.NetworkResponseModel

data class KeywordListResponse(
  val id: Int,
  val keywords: List<Keyword>
) : NetworkResponseModel
