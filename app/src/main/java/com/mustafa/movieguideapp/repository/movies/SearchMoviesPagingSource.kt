package com.mustafa.movieguideapp.repository.movies

import androidx.paging.rxjava2.RxPagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.models.entity.MovieRecentQueries
import com.mustafa.movieguideapp.models.network.DiscoverMovieResponse
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@OpenForTesting
class SearchMoviesPagingSource @Inject constructor(
    private val service: TheDiscoverService,
    private val movieDao: MovieDao,
    private val query: String,
    private val search: Boolean
) : RxPagingSource<Int, Movie>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Movie>> {
        val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX

        return service.fetchSearchMovies(query = query, page = currentLoadingPageKey)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { if (search) movieDao.insertQuery(MovieRecentQueries(query)) }
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