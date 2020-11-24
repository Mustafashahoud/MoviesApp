package com.mustafa.movieguideapp.repository.tvs

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_API_PAGE_SIZE
import kotlinx.coroutines.flow.Flow
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

    fun loadFilteredTvs(
        filterData: FilterData,
        totalCount: (Int) -> Unit
    ): LiveData<PagingData<Tv>> {
        return Pager(
            config = PagingConfig(
                TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            FilteredTvsPagingSource(service, filterData) {
                totalCount(it)
            }
        }.liveData
    }
}