package com.mustafa.movieguideapp.room

import android.util.SparseIntArray
import androidx.collection.SparseArrayCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mustafa.movieguideapp.models.entity.*

@Dao
abstract class MovieDao {

    @Query("SELECT * FROM Movie WHERE page in (:pages) AND search = 0 ")
    abstract suspend fun loadDiscoveryMovieListByPage(pages: List<Int>): List<Movie>

    @Query("SELECT * FROM Movie WHERE id in (:ids) AND search = 1 AND poster_path <> '' order by page ")
    abstract suspend fun loadSearchMoviesList(ids: List<Int>): List<Movie>

    @Query("SELECT * FROM SearchMovieResult WHERE `query` = :query AND pageNumber = :page")
    abstract suspend fun searchMovieResult(query: String, page: Int): SearchMovieResult?

    @Query("SELECT * FROM FilteredMovieResult WHERE page = :pageNumber")
    abstract suspend fun getFilteredMovieResultByPage(pageNumber: Int): FilteredMovieResult?

    @Update
    abstract suspend fun updateMovie(movie: Movie)

    @Query("SELECT * FROM MOVIE WHERE id = :id_")
    abstract suspend fun getMovie(id_: Int): Movie

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMovieList(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertSearchMovieResult(result: SearchMovieResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertMovieRecentQuery(insertMovieRecentQuery: MovieRecentQueries)

    @Query("SELECT * FROM Movie WHERE id in (:movieIds) AND search = 0 AND filter = 1 AND poster_path <> '' ORDER BY page")
    abstract suspend fun loadFilteredMovieList(movieIds: List<Int>): List<Movie>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertFilteredMovieResult(result: FilteredMovieResult)

    @Query("SELECT * FROM MovieRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30 ")
    abstract suspend fun loadMovieRecentQueries(): List<MovieRecentQueries>

    @Query("DELETE FROM MovieRecentQueries")
    abstract suspend fun deleteAllMovieRecentQueries()

    @Query("SELECT * FROM Movie JOIN movieSuggestionsFts ON Movie.id == movieSuggestionsFts.id WHERE movieSuggestionsFts.title MATCH '%' || :text || '%' LIMIT 20")
    abstract suspend fun loadMovieSuggestions(text: String): List<Movie>

//    fun loadSearchMovieListOrdered(movieIds: List<Int>): LiveData<List<Movie>> {
//        val order = SparseIntArray() // SparseArrayCompat can be used .. but it would need mocking
//        movieIds.withIndex().forEach {
//            order.put(it.value, it.index)
//        }
//        return Transformations.map(loadSearchMovieList(movieIds)) { movies ->
//            movies.sortedWith(compareBy { order.get(it.id) })
//        }
//    }

//    fun loadDiscoveryMovieListOrdered2(movieIds: List<Int>): List<Movie> {
//        val order = SparseArrayCompat<Int>() // SparseArrayCompat can be used
//        movieIds.withIndex().forEach {
//            order.put(it.value, it.index)
//        }
//        return loadDiscoveryMovieList2(movieIds).sortedWith(compareBy { order.get(it.id) })
//    }

}

