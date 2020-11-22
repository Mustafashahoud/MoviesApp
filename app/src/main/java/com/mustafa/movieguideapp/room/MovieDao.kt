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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovieList(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSearchMovieResult(result: SearchMovieResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertDiscoveryMovieResult(result: DiscoveryMovieResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFilteredMovieResult(result: FilteredMovieResult)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovieRecentQuery(insertMovieRecentQuery: MovieRecentQueries)

    @Query("SELECT * FROM MovieRecentQueries GROUP BY `query` ORDER BY id DESC LIMIT 30 ")
    abstract fun loadMovieRecentQueries(): LiveData<List<MovieRecentQueries>>

    @Query("DELETE FROM MovieRecentQueries")
    abstract fun deleteAllMovieRecentQueries()

    @Query("SELECT * FROM DiscoveryMovieResult WHERE page = :pageNumber")
    abstract fun getDiscoveryMovieResultByPage(pageNumber: Int): DiscoveryMovieResult

    @Query("SELECT * FROM DiscoveryMovieResult WHERE page = :pageNumber")
    abstract fun getDiscoveryMovieResultByPageLiveData(pageNumber: Int): LiveData<DiscoveryMovieResult>

    @Query("SELECT * FROM FilteredMovieResult WHERE page = :pageNumber")
    abstract fun getFilteredMovieResultByPage(pageNumber: Int): FilteredMovieResult

    @Query("SELECT * FROM FilteredMovieResult WHERE page = :pageNumber")
    abstract fun getFilteredMovieResultByPageLiveData(pageNumber: Int): LiveData<FilteredMovieResult>

    @Query("SELECT * FROM Movie WHERE id in (:movieIds) AND search = 0 ")
    abstract fun loadDiscoveryMovieList(movieIds: List<Int>): LiveData<List<Movie>>

    @Query("SELECT * FROM Movie WHERE id in (:movieIds) AND search = 0 AND filter = 1 AND poster_path <> ''")
    abstract fun loadFilteredMovieList(movieIds: List<Int>): LiveData<List<Movie>>

    @Query("SELECT * FROM Tv WHERE id in (:tvIds) AND search = 0 AND filter = 1 AND poster_path <> ''")
    abstract fun loadFilteredTvList(tvIds: List<Int>): LiveData<List<Tv>>

    @Update
    abstract fun updateMovie(movie: Movie)

    @Query("SELECT * FROM MOVIE WHERE id = :id_")
    abstract fun getMovie(id_: Int): Movie

    @Query("SELECT * FROM Movie WHERE page = :page_ AND search = 0")
    abstract fun loadDiscoveryMovieList(page_: Int): LiveData<List<Movie>>

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
        val order = SparseArrayCompat<Int>() // SparseArrayCompat can be used
        movieIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(
            loadDiscoveryMovieList(movieIds)) { movies ->
            movies.sortedWith(compareBy { order.get(it.id) })
        }
    }

    fun loadFilteredMovieListOrdered(movieIds: List<Int>): LiveData<List<Movie>> {
        val order = SparseIntArray() // SparseArrayCompat can be used
        movieIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(
            loadFilteredMovieList(movieIds)) { movies ->
            movies.sortedWith(compareBy { order.get(it.id) })
        }
    }

    fun loadFilteredTvListOrdered(tvIds: List<Int>): LiveData<List<Tv>> {
        val order = SparseIntArray() // SparseArrayCompat can be used
        tvIds.withIndex().forEach {
            order.put(it.value, it.index)
        }
        return Transformations.map(
            loadFilteredTvList(tvIds)) { tvs ->
            tvs.sortedWith(compareBy { order.get(it.id) })
        }
    }


    @Query("SELECT * FROM Movie JOIN movieSuggestionsFts ON Movie.id == movieSuggestionsFts.id WHERE movieSuggestionsFts.title MATCH '%' || :text || '%' LIMIT 20" )
    abstract fun loadMovieSuggestions(text: String): LiveData<List<Movie>>
}