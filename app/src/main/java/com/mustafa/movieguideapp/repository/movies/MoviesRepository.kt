package com.mustafa.movieguideapp.repository.movies

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_API_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class MoviesRepository @Inject constructor(
    private val service: TheDiscoverService,
    private val movieDao: MovieDao
) {
    fun loadPopularMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) { MoviesPagingSource(service) }.flow
    }

    fun searchMovies(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            SearchMoviesPagingSource(
                service,
                movieDao,
                query = query,
                search = true
            )
        }.flow
    }

    fun getMovieSuggestions(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            SearchMoviesPagingSource(
                service,
                movieDao,
                query = query,
                search = false
            )
        }.flow
    }

    suspend fun getMovieRecentQueries(): List<String> {
        return movieDao.getAllMovieQueries()
    }

    suspend fun deleteAllMovieRecentQueries() {
        movieDao.deleteAllMovieQueries()
    }

    fun loadFilteredMovies(
        filterData: FilterData,
        totalCount: (Int) -> Unit
    ): LiveData<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            FilteredMoviesPagingSource(service, filterData) {
                totalCount(it)
            }
        }.liveData
    }
}