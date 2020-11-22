package com.mustafa.movieguideapp.repository.tvs

import androidx.paging.PagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Tv
import com.mustafa.movieguideapp.models.entity.TvRecentQueries
import com.mustafa.movieguideapp.room.TvDao
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchTvsPagingSource @Inject constructor(
    private val service: TheDiscoverService,
    private val tvDao: TvDao,
    private val query: String,
    private val search: Boolean
) : PagingSource<Int, Tv>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Tv> {
        return try {
            val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
            val response = service.fetchSearchTvs(query = query, page = currentLoadingPageKey)
            val tvs = response.results

            // if it did not throw exception, that means it is okay --> save it
            if (search) tvDao.insertQuery(TvRecentQueries(query))

            LoadResult.Page(
                data = tvs,
                prevKey = if (currentLoadingPageKey == TMDB_STARTING_PAGE_INDEX) null else currentLoadingPageKey - 1,
                nextKey = if (tvs.isEmpty() || response.page >= response.total_pages) null else currentLoadingPageKey.plus(
                    1
                )
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