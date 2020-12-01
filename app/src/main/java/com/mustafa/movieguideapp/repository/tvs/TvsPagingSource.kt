package com.mustafa.movieguideapp.repository.tvs


import androidx.paging.rxjava2.RxPagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.models.network.DiscoverTvResponse
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenForTesting
class TvsPagingSource @Inject constructor(private val backend: TheDiscoverService) :
    RxPagingSource<Int, Tv>() {
    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Tv>> {
        val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX

        return backend.fetchTvs(page = currentLoadingPageKey)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, currentLoadingPageKey) }
            .onErrorReturn { LoadResult.Error(it) }

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