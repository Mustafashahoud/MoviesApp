package com.mustafa.movieguideapp.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movie_remote_keys")
data class MovieRemoteKeys(
    @PrimaryKey
    var movieId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)