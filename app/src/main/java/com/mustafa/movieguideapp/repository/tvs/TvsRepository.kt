package com.mustafa.movieguideapp.repository.tvs

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.models.entity.TvRecentQueries
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_API_PAGE_SIZE
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class TvsRepository @Inject constructor(
    private val service: TheDiscoverService,
    private val tvDao: TvDao
) {


    fun loadPopularTvs(): Flowable<PagingData<Tv>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE
            )
        ) {
            TvsPagingSource(
                service
            )
        }.flowable
    }


    fun searchTvs(query: String): Flowable<PagingData<Tv>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE
            )
        ) {
            SearchTvsPagingSource(
                service = service,
                query = query
            )
        }.flowable
    }

    fun getTvSuggestions(query: String): LiveData<PagingData<Tv>> {
        return LiveDataReactiveStreams.fromPublisher(
            Pager(
                config = PagingConfig(
                    TMDB_API_PAGE_SIZE,
                    enablePlaceholders = false
                )
            ) {
                SearchTvsPagingSource(
                    service,
                    query = query
                )
            }.flowable
        )
    }


    fun loadFilteredTvs(
        filterData: FilterData,
        totalCount: (Int) -> Unit
    ): LiveData<PagingData<Tv>> {
        return LiveDataReactiveStreams.fromPublisher(
            Pager(
                config = PagingConfig(
                    TMDB_API_PAGE_SIZE,
                    enablePlaceholders = false
                )
            ) {
                FilteredTvsPagingSource(service, filterData) {
                    totalCount(it)
                }
            }.flowable
        )
    }

    suspend fun getTvRecentQueries(): List<String> {
        return tvDao.getAllTvQueries()
    }

    suspend fun deleteAllTvRecentQueries() {
        tvDao.deleteAllTvQueries()
    }

    suspend fun saveQuery(query: String) {
        tvDao.insertQuery(TvRecentQueries(query))
    }
}