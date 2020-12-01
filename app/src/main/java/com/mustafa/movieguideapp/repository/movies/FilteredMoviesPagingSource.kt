package com.mustafa.movieguideapp.repository.movies

import androidx.paging.rxjava2.RxPagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.models.network.DiscoverMovieResponse
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenForTesting
class FilteredMoviesPagingSource @Inject constructor(
    private val service: TheDiscoverService,
    private val filterData: FilterData,
    private val totalCount: (Int) -> Unit
) : RxPagingSource<Int, Movie>() {
    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Movie>> {
        val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
        return service.searchMovieFilters(
            page = currentLoadingPageKey,
            rating = filterData.rating,
            region = filterData.region,
            sort = filterData.sort,
            with_genres = filterData.genres,
            with_keywords = filterData.keywords,
            with_original_language = filterData.language,
            with_runtime = filterData.runtime,
            year = filterData.year
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess { totalCount(it.total_results)}
            .map { toLoadResult(it, currentLoadingPageKey) }
            .onErrorReturn { LoadResult.Error(it) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun toLoadResult(
        response: DiscoverMovieResponse,
        currentLoadingPageKey: Int
    ): LoadResult<Int, Movie> {
        return LoadResult.Page(
            data = response.results,
            prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1,
            nextKey = if (currentLoadingPageKey >= response.total_pages) null else currentLoadingPageKey + 1
        )
    }

}
