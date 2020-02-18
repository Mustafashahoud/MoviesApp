package com.mustafa.movieapp.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TvRecentQueries(
    val query: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}