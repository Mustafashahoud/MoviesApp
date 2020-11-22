package com.mustafa.movieguideapp.repository.people

import androidx.paging.PagingSource
import com.mustafa.movieguideapp.api.PeopleService
import com.mustafa.movieguideapp.models.Person
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.utils.Constants.Companion.TMDB_STARTING_PAGE_INDEX
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class SearchPeoplePagingSource @Inject constructor(
    private val service: PeopleService,
    private val peopleDao: PeopleDao? = null,
    private val query: String,
    private val search: Boolean
) : PagingSource<Int, Person>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Person> {
        return try {
            val currentLoadingPageKey = params.key ?: TMDB_STARTING_PAGE_INDEX
            val response = service.fetchPopularPeople2(page = currentLoadingPageKey)
            val people = response.results

            if (search) peopleDao?.insertQuery(PeopleRecentQueries(query))

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