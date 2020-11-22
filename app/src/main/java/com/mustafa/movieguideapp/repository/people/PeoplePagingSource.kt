package com.mustafa.movieguideapp.repository.people

import androidx.paging.PagingSource
import com.mustafa.movieguideapp.api.PeopleService
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PeoplePagingSource @Inject constructor(private val backend: PeopleService) :
    PagingSource<Int, Person>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> {
        return try {
            val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
            val response = backend.fetchPopularPeople2(page = currentLoadingPageKey)
            val people = response.results

            LoadResult.Page(
                data = people,
                prevKey = if (currentLoadingPageKey == TMDB_STARTING_PAGE_INDEX) null else currentLoadingPageKey - 1,
                nextKey = if (people.isEmpty() || response.page >= response.total_pages) null else currentLoadingPageKey.plus(
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