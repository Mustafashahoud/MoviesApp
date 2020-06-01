package com.mustafa.movieguideapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mustafa.movieguideapp.api.ApiResponse
import com.mustafa.movieguideapp.api.PeopleService
import com.mustafa.movieguideapp.mappers.*
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.*
import com.mustafa.movieguideapp.models.network.*
import com.mustafa.movieguideapp.room.AppDatabase
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.AbsentLiveData
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class PeopleRepository @Inject constructor(
    private val peopleService: PeopleService,
    private val peopleDao: PeopleDao,
    private val db: AppDatabase,
    private val appExecutors: AppExecutors
)  {

    fun loadPeople(page: Int): LiveData<Resource<List<Person>>> {
        return object :
            NetworkBoundResource<List<Person>, PeopleResponse, PeoplePagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: PeopleResponse) {

                val ids = arrayListOf<Int>()
                val personIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = false
                }
                if (page != 1) {
                    val prevPageNumber = page - 1
                    val peopleResult =
                        peopleDao.getPeopleResultByPage(prevPageNumber)
                    ids.addAll(peopleResult.ids)
                }

                ids.addAll(personIds)

                val peopleResult = PeopleResult(
                    ids = ids,
                    page = page
                )
                db.run {
                    peopleDao.insertPeopleResult(peopleResult)
                    peopleDao.insertPeople(people = items.results)
                }

            }

            override fun shouldFetch(data: List<Person>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Person>> {
                return Transformations.switchMap(peopleDao.getPeopleResultByPageLiveData(page)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        peopleDao.loadPeopleListOrdered(searchData.ids)
                    }
                }
            }

            override fun pageChecker(): PeoplePagingChecker {
                return PeoplePagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<PeopleResponse>> {
                return peopleService.fetchPopularPeople(page = page)
            }
        }.asLiveData()
    }

    fun loadPersonDetail(id: Int): LiveData<Resource<PersonDetail>> {
        return object : NetworkBoundResource<
                PersonDetail,
                PersonDetail,
                PersonDetailPagingChecker
                >(appExecutors) {
            override fun saveCallResult(items: PersonDetail) {
                val person = peopleDao.getPerson(id_ = id)
                person.personDetail = items
                peopleDao.updatePerson(person = person)
            }

            override fun shouldFetch(data: PersonDetail?): Boolean {
                return data == null || data.biography.isEmpty()
            }

            override fun loadFromDb(): LiveData<PersonDetail> {
                val person = peopleDao.getPerson(id_ = id)
                val data: MutableLiveData<PersonDetail> = MutableLiveData()
                data.value = person.personDetail
                return data
            }

            override fun pageChecker(): PersonDetailPagingChecker {
                return PersonDetailPagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<PersonDetail>> {
                return peopleService.fetchPersonDetail(id = id)
            }
        }.asLiveData()
    }

    fun searchPeople(query: String, page: Int): LiveData<Resource<List<Person>>> {
        return object :
            NetworkBoundResource<List<Person>, PeopleResponse, PeoplePagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: PeopleResponse) {

                val ids = arrayListOf<Int>()
                val personIds: List<Int> = items.results.map { it.id }

                for (item in items.results) {
                    item.page = page
                    item.search = true
                }
                if (page > 1) {
                    val prevPageNumber = page - 1
                    val peopleSearchResult = peopleDao.searchPeopleResult(query, prevPageNumber)
                    ids.addAll(peopleSearchResult.ids)
                }

                ids.addAll(personIds)

                val searchPeopleResult = SearchPeopleResult(
                query = query,
                ids = ids,
                page = page
                )
                val recentQueries = PeopleRecentQueries(query)

                db.runInTransaction {
                    peopleDao.insertPeopleRecentQuery(recentQueries)
                    peopleDao.insertSearchPeopleResult(searchPeopleResult)
                    peopleDao.insertPeople(people = items.results)
                }

            }

            override fun shouldFetch(data: List<Person>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<Person>> {
                return Transformations.switchMap(peopleDao.searchPeopleResultLiveData(query, page)) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        peopleDao.loadSearchPeopleListOrdered(searchData.ids)
                    }
                }
            }

            override fun pageChecker(): PeoplePagingChecker {
                return PeoplePagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<PeopleResponse>> {
                return peopleService.searchPeople(query, page = page)
            }
        }.asLiveData()
    }


    fun loadMoviesForPerson(personId: Int): LiveData<Resource<List<MoviePerson>>> {
        return object :
            NetworkBoundResource<List<MoviePerson>, MoviePersonResponse, MoviePersonPagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: MoviePersonResponse) {

                val movieIds: List<Int> = items.cast.map { it.id }

                peopleDao.insertMovieForPerson(movies = items.cast)
                val moviePersonResult = MoviePersonResult(
                    moviesIds = movieIds,
                    personId = personId
                )
                peopleDao.insertMoviePersonResult(moviePersonResult)
            }

            override fun shouldFetch(data: List<MoviePerson>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<MoviePerson>> {
                return Transformations.switchMap(
                    peopleDao.getMoviePersonResultByPersonIdLiveData(
                        personId
                    )
                ) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        peopleDao.loadMoviesForPerson(searchData.moviesIds)
                    }
                }
            }

            override fun pageChecker(): MoviePersonPagingChecker {
                return MoviePersonPagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<MoviePersonResponse>> {
                return peopleService.fetchPersonMovies(id = personId)
            }
        }.asLiveData()
    }

    fun loadTvsForPerson(personId: Int): LiveData<Resource<List<TvPerson>>> {
        return object :
            NetworkBoundResource<List<TvPerson>, TvPersonResponse, TvPersonPagingChecker>(
                appExecutors
            ) {
            override fun saveCallResult(items: TvPersonResponse) {

                val movieIds: List<Int> = items.cast.map { it.id }

                peopleDao.insertTvForPerson(tvs = items.cast)
                val tvPersonResult = TvPersonResult(
                    tvsIds = movieIds,
                    personId = personId
                )
                peopleDao.insertTvPersonResult(tvPersonResult)
            }

            override fun shouldFetch(data: List<TvPerson>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<TvPerson>> {
                return Transformations.switchMap(
                    peopleDao.getTvPersonResultByPersonIdLiveData(
                        personId
                    )
                ) { searchData ->
                    if (searchData == null) {
                        AbsentLiveData.create()
                    } else {
                        peopleDao.loadTvsForPerson(searchData.tvsIds)
                    }
                }
            }

            override fun pageChecker(): TvPersonPagingChecker {
                return TvPersonPagingChecker()
            }

            override fun createCall(): LiveData<ApiResponse<TvPersonResponse>> {
                return peopleService.fetchPersonTvs(id = personId)
            }
        }.asLiveData()
    }

    fun getPeopleSuggestionsFromRoom(query: String?): LiveData<List<Person>> {
        val peopleQuery = MutableLiveData<String>()
        peopleQuery.value = query
        return Transformations.switchMap(peopleQuery) {
            if (it.isNullOrBlank()) {
                AbsentLiveData.create()
            } else {
                peopleDao.loadPeopleSuggestions(it)
            }
        }

    }


    fun getPeopleRecentQueries() = peopleDao.loadPeopleRecentQueries()
    fun deleteAllPeopleRecentQueries() =
        peopleDao.deleteAllPeopleRecentQueries()
}
