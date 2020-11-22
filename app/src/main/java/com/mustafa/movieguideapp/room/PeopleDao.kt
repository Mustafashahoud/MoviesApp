package com.mustafa.movieguideapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries

@Dao
abstract class PeopleDao {
    @Insert
    abstract suspend fun insertQuery(query: PeopleRecentQueries)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT `query` FROM PeopleRecentQueries ORDER BY id DESC LIMIT 30")
    abstract suspend fun getAllPeopleQueries(): List<String>

    @Query("DELETE FROM PeopleRecentQueries")
    abstract suspend fun deleteAllPeopleQueries()
}
