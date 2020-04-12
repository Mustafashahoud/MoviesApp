package com.mustafa.movieapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mustafa.movieapp.api.*
import com.mustafa.movieapp.mappers.MoviePagingChecker
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.DiscoveryMovieResult
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.MovieRecentQueries
import com.mustafa.movieapp.models.entity.SearchMovieResult
import com.mustafa.movieapp.models.network.DiscoverMovieResponse
import com.mustafa.movieapp.room.AppDatabase
import com.mustafa.movieapp.room.MovieDao
import com.mustafa.movieapp.room.TvDao
import com.mustafa.movieapp.utils.AbsentLiveData
import com.mustafa.movieapp.utils.RateLimiter
import com.mustafa.movieapp.view.ui.common.AppExecutors
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepository @Inject constructor(
    private val discoverService: TheDiscoverService,
    private val movieDao: MovieDao,
    private val db: AppDatabase,
    private val tvDao: TvDao,
    private val appExecutors: AppExecutors
)  {

    private val photoListRateLimit = RateLimiter<String>(1, TimeUnit.DAYS)

    fun loadMovies(page: Int): LiveData<Resource<List<Movie>>> {
        return object :
            NetworkBoundResource<List<Movie>, DiscoverMovieResponse, MoviePagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: DiscoverMovieResponse) {

                val ids = arrayListOf<Int>()
                val movieIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search =
                        false // it is discovery movie I wanna differentiate cus discovery is sorted by popularity
                }
                if (page != 1) {
                    val prevPageNumber = page - 1
                    val discoveryMovieResult =
                        movieDao.getDiscoveryMovieResultByPage(prevPageNumber)
                    ids.addAll(discoveryMovieResult.ids)
                }

                ids.addAll(movieIds)
                movieDao.insertMovieList(movies = items.results)
                val discoveryMovieResult = DiscoveryMovieResult(
                    ids = ids,
                    page = page
                )
                movieDao.insertDiscoveryMovieResult(discoveryMovieResult)
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return Transformations.switchMap(movieDao.getDiscoveryMovieResultByPageLiveData(page)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        movieDao.loadDiscoveryMovieListOrdered(searchData.ids)
                    }
                }
            }

            override fun pageChecker(): MoviePagingChecker {
                return MoviePagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<DiscoverMovieResponse>> {
                return discoverService.fetchDiscoverMovie(page = page)
            }
        }.asLiveData()
    }

    fun searchMovies(query: String, page: Int): LiveData<Resource<List<Movie>>> {
        return object :
            NetworkBoundResource<List<Movie>, DiscoverMovieResponse, MoviePagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: DiscoverMovieResponse) {

                val ids = arrayListOf<Int>()
                val movieIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = true
                }

                if (page > 1) {
                    val prevPageNumber = page - 1
                    val movieSearchResult = movieDao.searchMovieResult(query, prevPageNumber)
                    ids.addAll(movieSearchResult.movieIds)
                }

                ids.addAll(movieIds)
                val searchMovieResult = SearchMovieResult(
                    query = query,
                    movieIds = ids,
                    pageNumber = page
                )

                val recentQueries = MovieRecentQueries(query)

                db.runInTransaction {
                    movieDao.insertMovieList(items.results)
                    movieDao.insertSearchMovieResult(searchMovieResult)
                    movieDao.insertMovieRecentQuery(recentQueries)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return Transformations.switchMap(
                    movieDao.searchMovieResultLiveData(
                        query,
                        page
                    )
                ) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        movieDao.loadSearchMovieListOrdered(searchData.movieIds)
                    }
                }
            }

            override fun pageChecker(): MoviePagingChecker {
                return MoviePagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<DiscoverMovieResponse>> {
                return discoverService.searchMovies(query, page = page)
            }
        }.asLiveData()
    }


    fun getMovieSuggestions(query: String, page: Int): LiveData<List<Movie>> {
        val results = MediatorLiveData<List<Movie>>()
        val response = discoverService.searchMovies(query, page)
        results.addSource(response) {
            when (response.value) {
                is ApiSuccessResponse -> {
                    results.value =
                        (response.value as ApiSuccessResponse<DiscoverMovieResponse>).body.results
                }
                is ApiEmptyResponse -> {
                    results.value = null
                }
                is ApiErrorResponse -> {
                    results.value = null
                }
            }
        }
        return results
    }


    fun getMovieSuggestionsFromRoom(query: String?): LiveData<List<Movie>> {
        val movieQuery = MutableLiveData<String>()
        movieQuery.value = query
        return Transformations.switchMap(movieQuery){
            if (it.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                movieDao.loadMovieSuggestions(it)
            }
        }

    }

    fun getMovieRecentQueries() = movieDao.loadMovieRecentQueries()
    fun deleteAllMovieRecentQueries() =
        movieDao.deleteAllMovieRecentQueries()

}