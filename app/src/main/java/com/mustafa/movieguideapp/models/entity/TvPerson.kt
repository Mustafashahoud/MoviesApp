package com.mustafa.movieguideapp.models.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(indices = [Index("id")])
data class TvPerson(
    @PrimaryKey
    val id: Int,
    var character: String,
    var credit_id: String,
    val poster_path: String?,
    val overview: String?,
    val first_air_date: String?,
    var genre_ids: List<Int>,
    val original_name: String,
    val original_language: String,
    val name: String,
    val episode_count: Int,
    val backdrop_path: String?,
    val popularity: Float,
    val vote_count: Int,
    val vote_average: Float
) : Parcelable