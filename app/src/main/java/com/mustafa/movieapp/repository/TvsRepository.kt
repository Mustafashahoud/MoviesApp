package com.mustafa.movieapp.repository

import androidx.lifecycle.*
import com.mustafa.movieapp.api.*
import com.mustafa.movieapp.mappers.MoviePagingChecker
import com.mustafa.movieapp.mappers.TvPagingChecker
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.*
import com.mustafa.movieapp.models.network.DiscoverMovieResponse
import com.mustafa.movieapp.models.network.DiscoverTvResponse
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
class TvsRepository @Inject constructor(
    private val discoverService: TheDiscoverService,
    private val movieDao: MovieDao,
    private val db: AppDatabase,
    private val tvDao: TvDao,
    private val appExecutors: AppExecutors
) : Repository {

    private val photoListRateLimit = RateLimiter<String>(1, TimeUnit.DAYS)
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




    fun getTvSuggestionsFromRoom(query: String): LiveData<List<Tv>>{
        val tvQuery = MutableLiveData<String>()
        tvQuery.value = query
        return Transformations.switchMap(tvQuery){
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


    /**
     * Total count of filter results
     */
    private val totalFilteredResults = MutableLiveData<String>()
    fun getTotalFilteredResults(): LiveData<String> = totalFilteredResults
}
