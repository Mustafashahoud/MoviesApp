package com.mustafa.movieguideapp.repository.movies

import androidx.paging.PagingSource
import com.mustafa.movieguideapp.api.TheDiscoverService
import com.mustafa.movieguideapp.models.Movie
import com.mustafa.movieguideapp.models.entity.MovieRecentQueries
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OpenForTesting
class SearchMoviesPagingSource @Inject constructor(
    private val service: TheDiscoverService,
    private val movieDao: MovieDao,
    private val query: String,
    private val search: Boolean
) : PagingSource<Int, Movie>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        return try {
            val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
            val response = service.fetchSearchMovies(query = query, page = currentLoadingPageKey)

            // if it did not throw exception, that means it is okay --> save it
            if (search) movieDao.insertQuery(MovieRecentQueries(query))

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