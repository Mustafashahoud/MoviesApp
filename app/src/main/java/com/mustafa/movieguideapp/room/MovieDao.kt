package com.mustafa.movieguideapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.mustafa.movieguideapp.models.entity.MovieRecentQueries

@Dao
abstract class MovieDao {
    @Insert
    abstract suspend fun insertQuery(query: MovieRecentQueries)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT `query` FROM MovieRecentQueries ORDER BY id DESC LIMIT 30")
    abstract suspend fun getAllMovieQueries(): List<String>

    @Query("DELETE FROM MovieRecentQueries")
    abstract suspend fun deleteAllMovieQueries()

}

