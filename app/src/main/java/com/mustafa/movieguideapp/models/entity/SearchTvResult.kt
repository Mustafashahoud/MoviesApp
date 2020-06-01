package com.mustafa.movieguideapp.models.entity

import androidx.room.Entity


@Entity(primaryKeys = ["query", "pageNumber"])
data class SearchTvResult(
        val query: String,
        val tvIds: List<Int>,
        val pageNumber: Int
)
