package com.mustafa.movieguideapp.models.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = Movie::class)
@Entity(tableName =  "movieSuggestionsFts")
class MovieSuggestionsFts (val id: Int, val title: String)