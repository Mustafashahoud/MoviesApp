package com.mustafa.movieguideapp.repository.tvs

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mustafa.movieguideapp.api.*
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_API_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class TvsRepository @Inject constructor(
    private val service: TheDiscoverService,
    private val tvDao: TvDao
) {


    fun loadPopularTvs(): Flow<PagingData<Tv>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE
            )
        ) {
            TvsPagingSource(
                service
            )
        }.flow
    }


    fun searchTvs(query: String): Flow<PagingData<Tv>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE
            )
        ) {
            SearchTvsPagingSource(
                service = service,
                tvDao = tvDao,
                query = query,
                search = true
            )
        }.flow
    }

    fun getTvSuggestions(query: String): Flow<PagingData<Tv>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            SearchTvsPagingSource(
                service,
                tvDao,
                query = query,
                search = false
            )
        }.flow
    }

    suspend fun getTvRecentQueries(): List<String> {
        return tvDao.getAllTvQueries()
    }

    suspend fun deleteAllTvRecentQueries() {
        tvDao.deleteAllTvQueries()
    }

    /**
     * I won,t be using the paging library in here because of that
     * i am using recyclerVee inside nestedScrollView it needs a different handling
     */
//    fun loadFilteredTvs(filterData: FilterData, totalCount: (Int) -> Unit): LiveData<PagingData<Tv>> {
//        return Pager(
//            config = PagingConfig(
//                TMDB_API_PAGE_SIZE,
//                enablePlaceholders = false
//            )
//        ) {
//            FilteredTvsPagingSource(service, filterData) {
//                totalCount(it)
//            }
//        }.liveData
//    }

    fun loadFilteredTvs(page: Int, filterData: FilterData, totalCount: (Int) -> Unit
    ): Flow<Resource<List<Tv>>> {
        return flow {
            emit(Resource.Loading())
            service.searchTvFilters(
                page = page,
                rating = filterData.rating,
                sort = filterData.sort,
                with_genres = filterData.genres,
                with_keywords = filterData.keywords,
                with_original_language = filterData.language,
                with_runtime = filterData.runtime,
                year = filterData.year
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