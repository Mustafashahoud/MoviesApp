package com.mustafa.movieguideapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mustafa.movieguideapp.models.entity.MovieRemoteKeys


@Suppress("unused")
@Dao
interface MovieRemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MovieRemoteKeys>)

    @Query("SELECT * FROM movie_remote_keys WHERE movieId = :movieId")
    suspend fun remoteKeysMovieId(movieId: Int): MovieRemoteKeys?

    @Query("DELETE FROM movie_remote_keys")
    suspend fun clearMovieRemoteKeys()
}