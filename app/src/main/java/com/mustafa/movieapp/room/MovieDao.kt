package com.mustafa.movieapp.room

import android.util.SparseIntArray
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mustafa.movieapp.models.entity.DiscoveryMovieResult
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.RecentQueries
import com.mustafa.movieapp.models.entity.SearchMovieResult
import java.util.*

@Dao
abstract class MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovieList(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSearchMovieResult(result: SearchMovieResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDiscoveryMovieResult(result: DiscoveryMovieResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRecentQuery(insertRecentQuery: RecentQueries)

    @Query(
        "SELECT * FROM RecentQueries " +
            "GROUP BY query" +
            " ORDER BY id DESC LIMIT 20 ")
    abstract fun loadRecentQueries(): LiveData<List<RecentQueries>>

    @Query("DELETE FROM RecentQueries")
    abstract fun deleteAllRecentQueries()

    @Query("SELECT * FROM DiscoveryMovieResult WHERE page = :pageNumber")
    abstract fun getDiscoveryMovieResultByPage(pageNumber: Int): DiscoveryMovieResult

    @Query("SELECT * FROM DiscoveryMovieResult WHERE page = :pageNumber")
    abstract fun getDiscoveryMovieResultByPageLiveData(pageNumber: Int): LiveData<DiscoveryMovieResult>

    @Query("SELECT * FROM Movie WHERE id in (:movieIds) AND search = 0 ")
    abstract fun loadDiscoveryMovieList(movieIds: List<Int>): LiveData<List<Movie>>

    @Update
    abstract fun updateMovie(movie: Movie)

    @Query("SELECT * FROM MOVIE WHERE id = :id_")
    abstract fun getMovie(id_: Int): Movie

    @Query("SELECT * FROM Movie WHERE page = :page_ AND search = 0")
    abstract fun loadDiscoveryMovieList(page_: Int): LiveData<List<Movie>>

    @Query("SELECT id FROM Movie WHERE page = :page_ AND search = 0")
    abstract fun loadMovieIdsMovieList(page_: Int): List<Int>

    @Query("SELECT * FROM Movie WHERE id in (:movieIds) AND search = 0")
    abstract fun loadMovieIdsMovieList(movieIds: List<Int>): LiveData<List<Movie>>

    @Query("SELECT * FROM SearchMovieResult WHERE `query` = :query AND pageNumber = :pageNumber ")
    abstract fun searchMovieResultLiveData(
        query: String,
        pageNumber: Int
    ): LiveData<SearchMovieResult>

    @Query("SELECT * FROM SearchMovieResult WHERE `query` = :query AND pageNumber = :pageNumber ")
    abstract fun searchMovieResult(query: String, pageNumber: Int): SearchMovieResult

    @Query("SELECT * FROM Movie WHERE id in (:movieIds) AND search = 1 AND poster_path <> '' ")
    abstract fun loadSearchMovieList(movieIds: List<Int>): LiveData<List<Movie>>


    fun loadSearchMovieListOrdered(movieIds: List<Int>): LiveData<List<Movie>> {
        val order = SparseIntArray() // SparseArrayCompat can be used .. but it would need mocking
        movieIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(loadSearchMovieList(movieIds)) { movies ->
            movies.sortedWith(compareBy { order.get(it.id) })
        }
    }

    fun loadDiscoveryMovieListOrdered(movieIds: List<Int>): LiveData<List<Movie>> {
        val order = SparseIntArray() // SparseArrayCompat can be used
        movieIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(
            loadDiscoveryMovieList(movieIds)) { movies ->
            movies.sortedWith(compareBy { order.get(it.id) })
        }
    }
}
