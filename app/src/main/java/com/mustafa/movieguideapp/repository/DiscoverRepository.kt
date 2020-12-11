package com.mustafa.movieguideapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mustafa.movieguideapp.api.ApiResponse
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.mappers.MoviePagingChecker
import com.mustafa.movieguideapp.mappers.TvPagingChecker
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.*
import com.mustafa.movieguideapp.models.network.DiscoverMovieResponse
import com.mustafa.movieguideapp.models.network.DiscoverTvResponse
import com.mustafa.movieguideapp.room.AppDatabase
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class DiscoverRepository @Inject constructor(
    private val discoverService: TheDiscoverService,
    private val movieDao: MovieDao,
    private val db: AppDatabase,
    private val tvDao: TvDao,
    private val appExecutors: AppExecutors
) {

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

    fun loadTvs(page: Int): LiveData<Resource<List<Tv>>> {
        return object :
            NetworkBoundResource<List<Tv>, DiscoverTvResponse, TvPagingChecker>(appExecutors) {
            override fun saveCallResult(items: DiscoverTvResponse) {
                val ids = arrayListOf<Int>()
                val tvIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search =
                        false // it is discovery movie I wanna differentiate cus discovery is sorted by popularity
                }
                if (page != 1) {
                    val prevPageNumber = page - 1
                    val discoveryTvResult =
                        tvDao.getDiscoveryTvResultByPage(prevPageNumber)
                    ids.addAll(discoveryTvResult.ids)
                }

                ids.addAll(tvIds)
                tvDao.insertTvList(tvs = items.results)
                val discoveryTvResult = DiscoveryTvResult(
                    ids = ids,
                    page = page
                )
                tvDao.insertDiscoveryTvResult(discoveryTvResult)
            }

            override fun shouldFetch(data: List<Tv>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Tv>> {
                return Transformations.switchMap(tvDao.getDiscoveryTvResultByPageLiveData(page)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        tvDao.loadDiscoveryTvListOrdered(searchData.ids)
                    }
                }
            }

            override fun pageChecker(): TvPagingChecker {
                return TvPagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<DiscoverTvResponse>> {
                return discoverService.fetchDiscoverTv(page = page)
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
                return true
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


    fun searchTvs(query: String, page: Int): LiveData<Resource<List<Tv>>> {
        return object :
            NetworkBoundResource<List<Tv>, DiscoverTvResponse, TvPagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: DiscoverTvResponse) {

                val ids = arrayListOf<Int>()
                val tvIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = true
                }

                if (page > 1) {
                    val prevPageNumber = page - 1
                    val tvSearchResult = tvDao.searchTvResult(query, prevPageNumber)
                    ids.addAll(tvSearchResult.tvIds)
                }

                ids.addAll(tvIds)
                val searchMovieResult = SearchTvResult(
                    query = query,
                    tvIds = ids,
                    pageNumber = page
                )

                val recentQueries = TvRecentQueries(query)

                db.runInTransaction {
                    tvDao.insertTvList(items.results)
                    tvDao.insertSearchTvResult(searchMovieResult)
                    tvDao.insertTvRecentQuery(recentQueries)
                }
            }

            override fun shouldFetch(data: List<Tv>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Tv>> {
                return Transformations.switchMap(
                    tvDao.searchTvResultLiveData(
                        query,
                        page
                    )
                ) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        tvDao.loadSearchTvListOrdered(searchData.tvIds)
                    }
                }
            }

            override fun pageChecker(): TvPagingChecker {
                return TvPagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<DiscoverTvResponse>> {
                return discoverService.searchTvs(query, page = page)
            }
        }.asLiveData()
    }

    fun getMovieSuggestionsFromRoom(query: String?): LiveData<List<Movie>> {
        val movieQuery = MutableLiveData<String>()
        movieQuery.value = query
        return Transformations.switchMap(movieQuery) {
            if (it.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                movieDao.loadMovieSuggestions(it)
            }
        }

    }

    fun getTvSuggestionsFromRoom(query: String): LiveData<List<Tv>> {
        val tvQuery = MutableLiveData<String>()
        tvQuery.value = query
        return Transformations.switchMap(tvQuery) {
            if (it.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                tvDao.loadTvSuggestions(it)
            }
        }
    }


    fun getMovieRecentQueries() = movieDao.loadMovieRecentQueries()
    fun deleteAllMovieRecentQueries() =
        movieDao.deleteAllMovieRecentQueries()

    fun getTvRecentQueries() = tvDao.loadTvRecentQueries()
    fun deleteAllTvRecentQueries() =
        tvDao.deleteAllTvRecentQueries()


    fun loadFilteredMovies(
        filterData: FilterData,
        page: Int,
        totalCount: (Int) -> Unit
    ): LiveData<Resource<List<Movie>>> {
        return object :
            NetworkBoundResource<List<Movie>, DiscoverMovieResponse, MoviePagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: DiscoverMovieResponse) {
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
                    val filterMovieResult =
                        movieDao.getFilteredMovieResultByPage(prevPageNumber)
                    ids.addAll(filterMovieResult.ids)
                }

                totalCount(items.total_results)

                ids.addAll(movieIds)
                movieDao.insertMovieList(movies = items.results)
                val filteredMovieResult = FilteredMovieResult(
                    ids = ids,
                    page = page
                )
                movieDao.insertFilteredMovieResult(filteredMovieResult)
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return Transformations.switchMap(movieDao.getFilteredMovieResultByPageLiveData(page)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        movieDao.loadFilteredMovieListOrdered(searchData.ids)
                    }
                }
            }

            override fun pageChecker(): MoviePagingChecker {
                return MoviePagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<DiscoverMovieResponse>> {
                return discoverService.searchMovieFilters(
                    filterData.rating,
                    filterData.sort,
                    filterData.year,
                    filterData.genres,
                    filterData.keywords,
                    filterData.language,
                    filterData.runtime,
                    filterData.region,
                    page
                )
            }
        }.asLiveData()
    }



    fun loadFilteredTvs(
        filterData: FilterData,
        page: Int,
        totalCount: (Int) -> Unit
    ): LiveData<Resource<List<Tv>>> {

        return object :
            NetworkBoundResource<List<Tv>, DiscoverTvResponse, TvPagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: DiscoverTvResponse) {
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
                    val filterTvResult =
                        tvDao.getFilteredTvResultByPage(prevPageNumber)
                    ids.addAll(filterTvResult.ids)
                }

                totalCount(items.total_results)

                ids.addAll(movieIds)
                tvDao.insertTvList(tvs = items.results)
                val filteredTvResult = FilteredTvResult(
                    ids = ids,
                    page = page
                )
                tvDao.insertFilteredTvResult(filteredTvResult)
            }

            override fun shouldFetch(data: List<Tv>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Tv>> {
                return Transformations.switchMap(tvDao.getFilteredTvResultByPageLiveData(page)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        movieDao.loadFilteredTvListOrdered(searchData.ids)
                    }
                }
            }

            override fun pageChecker(): TvPagingChecker {
                return TvPagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<DiscoverTvResponse>> {
                return discoverService.searchTvFilters(
                    filterData.rating,
                    filterData.sort,
                    filterData.year,
                    filterData.genres,
                    filterData.keywords,
                    filterData.language,
                    filterData.runtime,
                    page
                )
            }
        }.asLiveData()
    }
}
