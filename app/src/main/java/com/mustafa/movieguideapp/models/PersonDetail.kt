
package com.mustafa.movieguideapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PersonDetail(
  val birthday: String? = null,
  val known_for_department: String,
  val place_of_birth: String? = null,
  val also_known_as: List<String> = emptyList(),
  val biography: String
) : Parcelable, NetworkResponseModel
