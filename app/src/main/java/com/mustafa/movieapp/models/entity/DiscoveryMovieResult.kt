package com.mustafa.movieapp.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DiscoveryMovieResult(
    val ids: List<Int>,
    @PrimaryKey
    val page: Int
)