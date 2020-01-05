package com.mustafa.movieapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.mustafa.movieapp.api.ApiResponse
import com.mustafa.movieapp.api.TheDiscoverService
import com.mustafa.movieapp.mappers.MoviePagingChecker
import com.mustafa.movieapp.mappers.TvPagingChecker
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.models.entity.SearchMovieResult
import com.mustafa.movieapp.models.entity.Tv
import com.mustafa.movieapp.models.network.DiscoverMovieResponse
import com.mustafa.movieapp.models.network.DiscoverTvResponse
import com.mustafa.movieapp.room.AppDatabase
import com.mustafa.movieapp.room.MovieDao
import com.mustafa.movieapp.room.TvDao
import com.mustafa.movieapp.utils.AbsentLiveData
import com.mustafa.movieapp.view.ui.common.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscoverRepository @Inject constructor(
        private val discoverService: TheDiscoverService,
        private val movieDao: MovieDao,
        private val db: AppDatabase,
        private val tvDao: TvDao,
        private val appExecutors: AppExecutors
) : Repository {

    fun loadMovies(page: Int): LiveData<Resource<List<Movie>>> {
        return object : NetworkBoundResource<List<Movie>, DiscoverMovieResponse, MoviePagingChecker>(appExecutors) {
            override fun saveCallResult(items: DiscoverMovieResponse) {

                val movieIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = false // it is discovery movie I wanna differentiate cus discovery is sorted by popularity
                }
                movieDao.insertMovieList(movies = items.results)
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return movieDao.loadDiscoveryMovieListOrdered(page)
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
        return object : NetworkBoundResource<List<Tv>, DiscoverTvResponse, TvPagingChecker>(appExecutors) {
            override fun saveCallResult(items: DiscoverTvResponse) {
                for (item in items.results) {
                    item.page = page
                }
                tvDao.insertTv(tvs = items.results)
            }

            override fun shouldFetch(data: List<Tv>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Tv>> {
                return tvDao.getTvList(page_ = page)
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
        return object : NetworkBoundResource<List<Movie>, DiscoverMovieResponse, MoviePagingChecker>(appExecutors) {
            override fun saveCallResult(items: DiscoverMovieResponse) {

                val ids = arrayListOf<Int>()
                val movieIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = true
                }

                if (page > 1 ) {
                    val prevPageNumber = page - 1
                    val movieSearchResult = movieDao.searchMovieResult(query, prevPageNumber)
                    ids.addAll(movieSearchResult.movieIds)
                }

                ids.addAll(movieIds)
                val photoResult = SearchMovieResult(
                        query = query,
                        movieIds = ids,
                        pageNumber = page
                )

                db.runInTransaction {
                    movieDao.insertMovieList(items.results)
                    movieDao.insertSearchMovieResult(photoResult)
                }
            }

            override fun shouldFetch(data: List<Movie>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Movie>> {
                return Transformations.switchMap(movieDao.searchMovieResultLiveData(query, page)) { searchData ->
                    if (searchData == null ) {
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
}
