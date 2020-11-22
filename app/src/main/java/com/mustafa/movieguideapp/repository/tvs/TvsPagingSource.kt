package com.mustafa.movieguideapp.repository.tvs

import androidx.paging.PagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Tv
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TvsPagingSource @Inject constructor(private val backend: TheDiscoverService): PagingSource<Int, Tv>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tv> {
        return try {
            val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
            val response = backend.fetchTvs(page = currentLoadingPageKey)
            val tvs = response.results

            LoadResult.Page(
                data = tvs,
                prevKey = if (currentLoadingPageKey == TMDB_STARTING_PAGE_INDEX) null else currentLoadingPageKey - 1,
                nextKey = if (tvs.isEmpty() || response.page >= response.total_pages) null else currentLoadingPageKey.plus(1)
            )
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        private const val TMDB_STARTING_PAGE_INDEX = 1
    }
}