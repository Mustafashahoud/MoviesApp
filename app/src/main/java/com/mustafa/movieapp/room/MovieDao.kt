package com.mustafa.movieapp.room

import androidx.collection.SparseArrayCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.SearchMovieResult
import java.util.*

@Dao
abstract class MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovieList(movies: List<Movie>)

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

    @Query("SELECT * FROM Movie WHERE id in (:movieIds) AND search = 1")
    abstract fun loadSearchMovieList(movieIds: List<Int>): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSearchMovieResult(result: SearchMovieResult)

    fun loadSearchMovieListOrdered(movieIds: List<Int>): LiveData<List<Movie>> {
        val order =
            SparseArrayCompat<Int>() // SparseIntArray can be used .. but it would need mocking
        movieIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(
            loadSearchMovieList(movieIds),
            fun(movies: List<Movie>): List<Movie>? {
                @Suppress("JavaCollectionsStaticMethodOnImmutableList")
                Collections.sort(movies) { r1, r2 ->
                    val pos1 = order.get(r1.id)
                    val pos2 = order.get(r2.id)
                    pos1!! - pos2!!
                }
                return movies
            })
    }

    fun loadDiscoveryMovieListOrdered(page: Int): LiveData<List<Movie>> {
        val movieIds: List<Int> = loadMovieIdsMovieList(page)
        val order =
            SparseArrayCompat<Int>() // SparseIntArray can be used .. but it would need mocking
        movieIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(
            loadMovieIdsMovieList(movieIds),
            fun(movies: List<Movie>): List<Movie>? {
                @Suppress("JavaCollectionsStaticMethodOnImmutableList")
                Collections.sort(movies) { r1, r2 ->
                    val pos1 = order.get(r1.id)
                    val pos2 = order.get(r2.id)
                    pos1!! - pos2!!
                }
                return movies
            })
    }

}
