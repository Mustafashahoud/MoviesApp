package com.mustafa.movieguideapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mustafa.movieguideapp.models.entity.TvRecentQueries

@Dao
abstract class TvDao {
    @Insert
    abstract fun insertQuery(query: TvRecentQueries)

    @Query("SELECT `query` FROM TvRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30")
    abstract suspend fun getAllTvQueries(): List<String>

    @Query("DELETE FROM TvRecentQueries")
    abstract suspend fun deleteAllTvQueries()
}
