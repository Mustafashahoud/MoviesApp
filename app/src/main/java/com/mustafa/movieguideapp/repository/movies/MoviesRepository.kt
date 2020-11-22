package com.mustafa.movieguideapp.repository.movies

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mustafa.movieguideapp.api.*
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_API_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    /**
     * I won't be using the paging library in here because of that
     * i am using recyclerVee inside nestedScrollView it needs a different handling
     */
//    fun loadFilteredMovies(filterData: FilterData, totalCount: (Int) -> Unit ): Flow<PagingData<Movie>> {
//        return Pager(
//            config = PagingConfig(
//                TMDB_API_PAGE_SIZE,
//                enablePlaceholders = false
//            )
//        ) {
//            FilteredMoviesPagingSource(service, filterData) {
//                totalCount(it)
//            }
//        }.flow
//    }

    fun loadFilteredMovies(page: Int, filterData: FilterData, totalCount: (Int) -> Unit): Flow<Resource<List<Movie>>> {
        return flow {
            emit(Resource.Loading())
            service.searchMovieFilters(
                page = page,
                rating = filterData.rating,
                sort = filterData.sort,
                with_genres = filterData.genres,
                with_keywords = filterData.keywords,
                with_original_language = filterData.language,
                with_runtime = filterData.runtime,
                year = filterData.year,
                region = filterData.region
            ).apply {
                this.onSuccessSuspend {
                    data?.let {
                        totalCount(it.total_results)
                        emit(Resource.Success(it.results, page >= it.total_pages))
                    }
                }
                // handle the case when the API request gets an error response.
                // e.g. internal server error.
            }.onErrorSuspend {
                emit(Resource.Error(message()))

                // handle the case when the API request gets an exception response.
                // e.g. network connection error.
            }.onExceptionSuspend {
                emit(Resource.Error(message()))
            }
        }
    }
}