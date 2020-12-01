package com.mustafa.movieguideapp.repository.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_API_PAGE_SIZE
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class MoviesRepository @Inject constructor(
    private val service: TheDiscoverService,
    private val movieDao: MovieDao
) {

    fun loadPopularMovies(): Flowable<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) { MoviesPagingSource(service) }.flowable
    }


    fun searchMovies(query: String): Flowable<PagingData<Movie>> {
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
        }.flowable
    }

    fun getMovieSuggestions(query: String): LiveData<PagingData<Movie>> {
        return LiveDataReactiveStreams.fromPublisher(
            Pager(
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
            }.flowable
        )
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
        return LiveDataReactiveStreams.fromPublisher(
            Pager(
                config = PagingConfig(
                    TMDB_API_PAGE_SIZE,
                    enablePlaceholders = false
                )
            ) {
                FilteredMoviesPagingSource(service, filterData) {
                    totalCount(it)
                }
            }.flowable
        )
    }
}