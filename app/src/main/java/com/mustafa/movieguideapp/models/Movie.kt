
package com.mustafa.movieguideapp.models

import android.os.Parcelable
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
  @PrimaryKey
  val id: Int,
  var page: Int,
  var keywords: List<Keyword>? = ArrayList(),
  var videos: List<Video>? = ArrayList(),
  var reviews: List<Review>? = ArrayList(),
  val poster_path: String?,
  val adult: Boolean,
  val overview: String?,
  val release_date: String?,
  var genre_ids: List<Int>,
  val original_title: String,
  val original_language: String,
  val title: String,
  val backdrop_path: String?,
  val popularity: Float,
  val vote_count: Int,
  val video: Boolean,
  val vote_average: Float,
  var search: Boolean?,
  var filter: Boolean?
) : Parcelable {

  /**
   * Empty constructor will be used in Testing
   */
  @Ignore
  constructor(id: Int, title: String = "ANY_MOVIE") : this(
    id,
    1,
    null,
    null,
    null,
    "poster_path",
    false,
    "",
    "1992",
    listOf(),
    "",
    "",
    title,
    null,
    0F,
    0,
    false,
    0f,
    false,
    false
  )
}


