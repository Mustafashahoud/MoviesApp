package com.mustafa.movieguideapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.*
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class DiscoverRepository @Inject constructor(
    private val discoverService: TheDiscoverService,
    private val movieDao: MovieDao,
    private val tvDao: TvDao,
    private val dispatcherIO: CoroutineDispatcher
) {

    suspend fun loadMovies(page: Int): Flow<Resource<List<Movie>>> {
        return networkBoundResource(
            loadFromDb = {
                movieDao.loadDiscoveryMovieListOrdered((1..page).toList())
            },
            fetchFromNetwork = { discoverService.fetchMovies(page) },
            dispatcherIO = dispatcherIO,
            pagingChecker = {
                it.page < it.total_pages
            },
            saveFetchResult = { items ->
                items.results.forEach { item ->
                    item.page = page
                    item.search =
                        false // it is discovery movie I wanna differentiate cus discovery is sorted by popularity
                }
                movieDao.insertMovieList(movies = items.results)
            }
        )
    }

    suspend fun loadTvs(page: Int): Flow<Resource<List<Tv>>> {
        return networkBoundResource(
            loadFromDb = {
                tvDao.loadDiscoveryTvListByPage((1..page).toList())
            },
            fetchFromNetwork = { discoverService.fetchTvs(page) },
            dispatcherIO = dispatcherIO,
            pagingChecker = {
                it.page < it.total_pages
            },
            saveFetchResult = { items ->
                items.results.forEach { item ->
                    item.page = page
                    item.search =
                        false // it is discovery movie I wanna differentiate cus discovery is sorted by popularity
                }
                tvDao.insertTvList(tvs = items.results)
            }
        )
    }

    suspend fun searchMovies(query: String, page: Int): Flow<Resource<List<Movie>>> {
        return networkBoundResource(
            loadFromDb = {
                val searchMovieResult = movieDao.searchMovieResult(query, page)
                searchMovieResult?.let {
                    movieDao.loadSearchMoviesList(searchMovieResult.movieIds)
                } ?: ArrayList()
            },
            fetchFromNetwork = { discoverService.fetchSearchMovies(query, page = page) },
            dispatcherIO = dispatcherIO,
            pagingChecker = {
                it.page < it.total_pages
            },
            saveFetchResult = { items ->
                val ids = arrayListOf<Int>()
                val movieIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = true
                }

                if (page > 1) {
                    val prevPageNumber = page - 1
                    val movieSearchResult = movieDao.searchMovieResult(query, prevPageNumber)
                    if (movieSearchResult != null) {
                        ids.addAll(movieSearchResult.movieIds)
                    }
                }

                ids.addAll(movieIds)
                val searchMovieResult = SearchMovieResult(
                    query = query,
                    movieIds = ids,
                    pageNumber = page
                )

                val recentQueries = MovieRecentQueries(query)

                movieDao.insertMovieList(items.results)
                movieDao.insertSearchMovieResult(searchMovieResult)
                movieDao.insertMovieRecentQuery(recentQueries)

            }
        )
    }

    suspend fun searchTvs(query: String, page: Int): Flow<Resource<List<Tv>>> {
        return networkBoundResource(
            loadFromDb = {
                val tvMovieResult = tvDao.searchTvResult(query, page)
                tvMovieResult?.let {
                    tvDao.loadSearchTvsList(tvMovieResult.tvIds)
                } ?: ArrayList()
            },
            fetchFromNetwork = { discoverService.fetchSearchTvs(query, page = page) },
            dispatcherIO = dispatcherIO,
            pagingChecker = {
                it.page < it.total_pages
            },
            saveFetchResult = { items ->
                val ids = arrayListOf<Int>()
                val tvIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = true
                }

                if (page > 1) {
                    val prevPageNumber = page - 1
                    val tvSearchResult = tvDao.searchTvResult(query, prevPageNumber)
                    if (tvSearchResult != null) {
                        ids.addAll(tvSearchResult.tvIds)
                    }
                }

                ids.addAll(tvIds)
                val searchTvResult = SearchTvResult(
                    query = query,
                    tvIds = ids,
                    pageNumber = page
                )

                val recentQueries = TvRecentQueries(query)

                tvDao.insertTvList(items.results)
                tvDao.insertSearchTvResult(searchTvResult)
                tvDao.insertTvRecentQuery(recentQueries)
            }
        )
    }


    suspend fun getMovieSuggestionsFromRoom(query: String?): LiveData<List<Movie>> {
        val movieSuggestions = MutableLiveData<List<Movie>>()
        withContext(dispatcherIO) {
            query?.let {
                val movies = movieDao.loadMovieSuggestions(query)
                if (!movies.isNullOrEmpty()) movieSuggestions.postValue(movies)
            }
        }
        return movieSuggestions
    }

    suspend fun getTvSuggestionsFromRoom(query: String?): LiveData<List<Tv>> {
        val tvSuggestions = MutableLiveData<List<Tv>>()
        withContext(dispatcherIO) {
            query?.let {
                val tvs = tvDao.loadTvSuggestions(query)
                if (!tvs.isNullOrEmpty()) tvSuggestions.postValue(tvs)
            }
        }
        return tvSuggestions
    }

    suspend fun getMovieRecentQueries(): LiveData<List<MovieRecentQueries>> {
        val movieRecentQueriesLiveData = MutableLiveData<List<MovieRecentQueries>>()
        withContext(dispatcherIO) {
            val movieRecentQueries = movieDao.loadMovieRecentQueries()
            if (!movieRecentQueries.isNullOrEmpty()) movieRecentQueriesLiveData.postValue(
                movieRecentQueries
            )
        }
        return movieRecentQueriesLiveData
    }

    suspend fun deleteAllMovieRecentQueries() {
        withContext(dispatcherIO) {
            movieDao.deleteAllMovieRecentQueries()
        }
    }

    suspend fun getTvRecentQueries(): LiveData<List<TvRecentQueries>> {
        val tvRecentQueriesLiveData = MutableLiveData<List<TvRecentQueries>>()
        withContext(dispatcherIO) {
            val tvRecentQueries = tvDao.loadTvRecentQueries()
            if (!tvRecentQueries.isNullOrEmpty()) tvRecentQueriesLiveData.postValue(tvRecentQueries)
        }
        return tvRecentQueriesLiveData
    }

    suspend fun deleteAllTvRecentQueries() {
        withContext(dispatcherIO) {
            tvDao.deleteAllTvRecentQueries()
        }
    }

    /**
     * Total count of movie filter results
     */
    private val totalFilteredResults = MutableLiveData<String>()

    fun getTotalFilteredResults(): LiveData<String> = totalFilteredResults

    fun loadFilteredMovies(
        rating: Int? = null,
        sort: String? = null,
        year: Int? = null,
        keywords: String? = null,
        genres: String? = null,
        language: String? = null,
        runtime: Int? = null,
        region: String? = null,
        page: Int
    ): Flow<Resource<List<Movie>>> {
        return networkBoundResource(
            loadFromDb = {
                val filteredMovieResult = movieDao.getFilteredMovieResultByPage(page)
                filteredMovieResult?.let {
                    movieDao.loadFilteredMovieList(filteredMovieResult.ids)
                } ?: ArrayList()
            },
            fetchFromNetwork = {
                discoverService.searchMovieFilters(
                    rating, sort, year, genres, keywords, language, runtime, region, page
                )
            },
            dispatcherIO = dispatcherIO,
            pagingChecker = {
                it.page < it.total_pages
            },
            saveFetchResult = { items ->
                val ids = arrayListOf<Int>()
                val movieIds: List<Int> = items.results.map { it.id }
                for (item in items.results) {
                    item.page = page
                    item.filter = true
                    item.search =
                        false // it is discovery movie I wanna differentiate cus discovery is sorted by popularity
                }
                if (page != 1) {
                    val prevPageNumber = page - 1
                    val filterMovieResult = movieDao.getFilteredMovieResultByPage(prevPageNumber)
                    if (filterMovieResult != null) {
                        ids.addAll(filterMovieResult.ids)
                    }
                }

                if (totalFilteredResults.value != items.total_results.toString())
                    totalFilteredResults.postValue(items.total_results.toString())

                ids.addAll(movieIds)

                val filteredMovieResult = FilteredMovieResult(
                    ids = ids,
                    page = page
                )

                movieDao.insertMovieList(movies = items.results)
                movieDao.insertFilteredMovieResult(filteredMovieResult)
            }
        )
    }

    fun loadFilteredTvs(
        rating: Int? = null,
        sort: String? = null,
        year: Int? = null,
        keywords: String? = null,
        genres: String? = null,
        language: String? = null,
        runtime: Int? = null,
        page: Int
    ): Flow<Resource<List<Tv>>> {

        return networkBoundResource(
            loadFromDb = {
                val filteredTvResult = tvDao.getFilteredTvResultByPage(page)
                filteredTvResult?.let {
                    tvDao.loadFilteredTvList(filteredTvResult.ids)
                } ?: ArrayList()
            },
            fetchFromNetwork = {
                discoverService.searchTvFilters(
                    rating, sort, year, genres, keywords, language, runtime, page
                )
            },
            dispatcherIO = dispatcherIO,
            pagingChecker = {
                it.page < it.total_pages
            },
            saveFetchResult = { items ->
                val ids = arrayListOf<Int>()
                val tvIds: List<Int> = items.results.map { it.id }
                for (item in items.results) {
                    item.page = page
                    item.filter = true
                    item.search =
                        false // it is discovery movie I wanna differentiate cus discovery is sorted by popularity
                }
                if (page != 1) {
                    val prevPageNumber = page - 1
                    val filterMovieResult = movieDao.getFilteredMovieResultByPage(prevPageNumber)
                    if (filterMovieResult != null) {
                        ids.addAll(filterMovieResult.ids)
                    }
                }

                if (totalFilteredResults.value != items.total_results.toString())
                    totalFilteredResults.postValue(items.total_results.toString())

                ids.addAll(tvIds)

                val filteredTvResult = FilteredTvResult(
                    ids = ids,
                    page = page
                )
                tvDao.insertTvList(tvs = items.results)
                tvDao.insertFilteredTvResult(filteredTvResult)
            }
        )
    }

    /**
     * Total count of movie filter results
     */
    private val totalTvFilteredResults = MutableLiveData<String>()
    fun getTotalTvFilteredResults(): LiveData<String> = totalTvFilteredResults
}
