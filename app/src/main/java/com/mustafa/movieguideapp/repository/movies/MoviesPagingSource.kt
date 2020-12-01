package com.mustafa.movieguideapp.repository.movies

import androidx.paging.rxjava2.RxPagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.models.network.DiscoverMovieResponse
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenForTesting
class MoviesPagingSource @Inject constructor(private val backend: TheDiscoverService) :
    RxPagingSource<Int, Movie>() {
    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Movie>> {
        val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX

        return backend.fetchMovies(page = currentLoadingPageKey)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, currentLoadingPageKey) }
            .onErrorReturn { LoadResult.Error(it) }

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