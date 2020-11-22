package com.mustafa.movieguideapp.repository.movies

import androidx.paging.PagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OpenForTesting
class MoviesPagingSource @Inject constructor(private val backend: TheDiscoverService) :
    PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
            val response = backend.fetchMovies(page = currentLoadingPageKey)
            val movies = response.results

            LoadResult.Page(
                data = movies,
                prevKey = if (currentLoadingPageKey == TMDB_STARTING_PAGE_INDEX) null else currentLoadingPageKey - 1,
                nextKey = if (movies.isEmpty() || response.page >= response.total_pages) null else currentLoadingPageKey.plus(
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

}