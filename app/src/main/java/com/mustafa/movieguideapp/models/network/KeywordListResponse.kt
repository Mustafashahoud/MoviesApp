
package com.mustafa.movieguideapp.models.network

import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.NetworkResponseModel

data class KeywordListResponse(
  val id: Int,
  val keywords: List<Keyword>
) : NetworkResponseModel
