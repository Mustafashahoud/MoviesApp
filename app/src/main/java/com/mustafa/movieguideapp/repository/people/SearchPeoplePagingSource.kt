package com.mustafa.movieguideapp.repository.people

import androidx.paging.rxjava2.RxPagingSource
import com.mustafa.movieguideapp.api.PeopleService
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.models.network.PeopleResponse
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchPeoplePagingSource @Inject constructor(
    private val service: PeopleService,
    private val query: String
) : RxPagingSource<Int, Person>() {

    override fun loadSingle(params: LoadParams<Int>): Single<LoadResult<Int, Person>> {
        val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
        return service.searchPeople(query, page = currentLoadingPageKey)
            .subscribeOn(Schedulers.io())
            .map { toLoadResult(it, currentLoadingPageKey) }
            .onErrorReturn { LoadResult.Error(it) }
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun toLoadResult(
        response: PeopleResponse,
        currentLoadingPageKey: Int
    ): LoadResult<Int, Person> {
        return LoadResult.Page(
            data = response.results,
            prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1,
            nextKey = if (currentLoadingPageKey >= response.total_pages) null else currentLoadingPageKey + 1
        )
    }
}