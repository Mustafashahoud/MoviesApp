package com.mustafa.movieguideapp.models.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(indices = [Index("id")])
data class MoviePerson(
    @PrimaryKey
    val id: Int,
    var character: String,
    var credit_id: String,
    var video: Boolean,
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
    val vote_average: Float
) : Parcelable