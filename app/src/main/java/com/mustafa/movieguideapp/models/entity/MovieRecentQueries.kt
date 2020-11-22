package com.mustafa.movieguideapp.models.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(
        value = ["query"],
        unique = true
    )]
)
data class MovieRecentQueries(val query: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}