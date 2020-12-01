package com.mustafa.movieguideapp.repository.tvs

import androidx.paging.rxjava2.RxPagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.models.entity.TvRecentQueries
import com.mustafa.movieguideapp.models.network.DiscoverTvResponse
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchTvsPagingSource @Inject constructor(
    private val service: TheDiscoverService,
    private val tvDao: TvDao,
    private val query: String,
    private val search: Boolean
) : RxPagingSource<Int, Tv>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Tv>> {
        val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX

        return service.fetchSearchTvs(query = query, page = currentLoadingPageKey)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                if (search)
                    tvDao.insertQuery(TvRecentQueries(query))
            }.map { toLoadResult(it, currentLoadingPageKey) }
            .onErrorReturn { LoadResult.Error(it) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun toLoadResult(
        response: DiscoverTvResponse,
        currentLoadingPageKey: Int
    ): LoadResult<Int, Tv> {
        return LoadResult.Page(
            data = response.results,
            prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1,
            nextKey = if (currentLoadingPageKey >= response.total_pages) null else currentLoadingPageKey + 1
        )
    }
}