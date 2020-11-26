package com.mustafa.movieguideapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries

@Dao
interface PeopleDao {
    @Insert
    suspend fun insertQuery(query: PeopleRecentQueries)

    @Query("SELECT `query` FROM PeopleRecentQueries GROUP BY `query`  ORDER BY id DESC LIMIT 30")
    suspend fun getAllPeopleQueries(): List<String>

    @Query("DELETE FROM PeopleRecentQueries")
    suspend fun deleteAllPeopleQueries()
}
