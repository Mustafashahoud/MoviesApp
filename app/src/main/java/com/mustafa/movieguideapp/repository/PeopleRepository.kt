package com.mustafa.movieguideapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mustafa.movieguideapp.api.PeopleService
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.*
import com.mustafa.movieguideapp.models.network.PersonDetail
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class PeopleRepository @Inject constructor(
    private val peopleService: PeopleService,
    private val peopleDao: PeopleDao,
    private val dispatcherIO: CoroutineDispatcher
) {

    suspend fun loadPeople(page: Int): Flow<Resource<List<Person>>> {
        return networkBoundResource(
            loadFromDb = { peopleDao.loadPeopleList((1..page).toList()) },
            fetchFromNetwork = { peopleService.fetchPopularPeople(page) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { it.page < it.total_pages },
            saveFetchResult = { items ->
                items.results.forEach { item ->
                    item.page = page
                    item.search = false
                }
                peopleDao.insertPeople(people = items.results)
            }
        )
    }

    suspend fun loadPersonDetail(id: Int): Flow<Resource<PersonDetail>> {
        return networkBoundResource(
            loadFromDb = {
                val person = peopleDao.getPerson(id_ = id)
                if (person?.personDetail != null){
                    person.personDetail!!
                } else {
                    PersonDetail(known_for_department = "",biography = "")
                }
            },
            fetchFromNetwork = { peopleService.fetchPersonDetail(id = id) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val person = peopleDao.getPerson(id_ = id)
                person?.personDetail = items
                peopleDao.updatePerson(person = person)
            }
        )
    }

    suspend fun searchPeople(query: String, page: Int): Flow<Resource<List<Person>>> {
        return networkBoundResource(
            loadFromDb = {
                val searchPeopleResult = peopleDao.searchPeopleResult(query, page)
                searchPeopleResult?.let {
                    peopleDao.loadSearchPeopleList(searchPeopleResult.ids)
                } ?: ArrayList()
            },
            fetchFromNetwork = { peopleService.searchPeople(query, page = page) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { it.page < it.total_pages },
            saveFetchResult = { items ->
                val ids = arrayListOf<Int>()
                val personIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = true
                }
                if (page > 1) {
                    val prevPageNumber = page - 1
                    val peopleSearchResult = peopleDao.searchPeopleResult(query, prevPageNumber)
                    peopleSearchResult?.ids?.let { ids.addAll(it) }
                }

                ids.addAll(personIds)

                val searchPeopleResult = SearchPeopleResult(query, ids, page)
                val recentQueries = PeopleRecentQueries(query)

                peopleDao.insertPeopleRecentQuery(recentQueries)
                peopleDao.insertSearchPeopleResult(searchPeopleResult)
                peopleDao.insertPeople(people = items.results)
            }
        )
    }

    suspend fun loadMoviesForPerson(personId: Int): Flow<Resource<List<MoviePerson>>> {
        return networkBoundResource(
            loadFromDb = {
                val moviePersonResult = peopleDao.getMoviePersonResultByPersonId(personId)
                moviePersonResult?.let {
                    peopleDao.loadMoviesForPerson(moviePersonResult.moviesIds)
                } ?: ArrayList()
            },
            fetchFromNetwork = { peopleService.fetchPersonMovies(id = personId) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val movieIds: List<Int> = items.cast.map { it.id }
                val moviePersonResult = MoviePersonResult(movieIds, personId)
                peopleDao.insertMovieForPerson(movies = items.cast)
                peopleDao.insertMoviePersonResult(moviePersonResult)
            }
        )
    }

    suspend fun loadTvsForPerson(personId: Int): Flow<Resource<List<TvPerson>>> {
        return networkBoundResource(
            loadFromDb = {
                val tvPersonResult = peopleDao.getTvPersonResultByPersonId(personId)
                tvPersonResult?.let {
                    peopleDao.loadTvsForPerson(tvPersonResult.tvsIds)
                } ?: ArrayList()
            },
            fetchFromNetwork = { peopleService.fetchPersonTvs(id = personId) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val tvIds: List<Int> = items.cast.map { it.id }

                peopleDao.insertTvForPerson(tvs = items.cast)
                val tvPersonResult = TvPersonResult(
                    tvsIds = tvIds,
                    personId = personId
                )
                peopleDao.insertTvPersonResult(tvPersonResult)
            }
        )
    }

    suspend fun getPeopleSuggestionsFromRoom(query: String?): LiveData<List<Person>> {
        val peopleSuggestions = MutableLiveData<List<Person>>()
        withContext(dispatcherIO) {
            query?.let {
                val people = peopleDao.loadPeopleSuggestions(it)
                if (!people.isNullOrEmpty()) peopleSuggestions.postValue(people)
            }
        }
        return peopleSuggestions
    }

    suspend fun getPeopleRecentQueries(): LiveData<List<PeopleRecentQueries>> {
        val peopleRecentQueriesLiveData = MutableLiveData<List<PeopleRecentQueries>>()
        withContext(dispatcherIO) {
            val peopleRecentQueries = peopleDao.loadPeopleRecentQueries()
            if (!peopleRecentQueries.isNullOrEmpty()) {
                peopleRecentQueriesLiveData.postValue(peopleRecentQueries)
            }
        }
        return peopleRecentQueriesLiveData
    }

    suspend fun deleteAllPeopleRecentQueries() {
        withContext(dispatcherIO) {
            peopleDao.deleteAllPeopleRecentQueries()
        }
    }
}
