
package com.mustafa.movieapp.models.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity( indices = [Index("id")])
data class Movie(
  @PrimaryKey
  val id: Int,
  var page: Int,
  var keywords: List<Keyword>? = ArrayList(),
  var videos: List<Video>? = ArrayList(),
  var reviews: List<Review>? = ArrayList(),
  val poster_path: String?,
  val adult: Boolean,
  val overview: String,
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
  var search: Boolean?
) : Parcelable {

  /**
   * Empty constructor will be used in [@SuggestionsAdapter]
   */
  @Ignore
  constructor(id: Int, title: String, poster_path: String?, vote_average: Float, release_date: String?) : this(
    id,
    0,
    null,
    null,
    null,
    poster_path,
    false,
    "",
    release_date,
    listOf(),
    "",
    "",
    title,
    null,
    0F,
    0,
    false,
    vote_average,
    false
  )
}


