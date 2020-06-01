package com.mustafa.movieguideapp.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieRecentQueries(
    val query: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}