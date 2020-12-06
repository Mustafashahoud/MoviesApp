package com.mustafa.movieguideapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mustafa.movieguideapp.models.entity.MovieRecentQueries

@Dao
interface MovieDao {
    @Insert
    suspend fun insertQuery(query: MovieRecentQueries)

    @Query("SELECT `query` FROM MovieRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30")
    suspend fun getAllMovieQueries(): List<String>

    @Query("DELETE FROM MovieRecentQueries")
    suspend fun deleteAllMovieQueries()

}

