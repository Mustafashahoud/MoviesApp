package com.mustafa.movieguideapp.models.entity

import androidx.room.Entity

@Entity(primaryKeys = ["query", "page"])
data class SearchPeopleResult(
    val query: String,
    val ids: List<Int>,
    val page: Int
)