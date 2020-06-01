package com.mustafa.movieguideapp.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MoviePersonResult(
    val moviesIds: List<Int>,
    @PrimaryKey
    val personId: Int
)