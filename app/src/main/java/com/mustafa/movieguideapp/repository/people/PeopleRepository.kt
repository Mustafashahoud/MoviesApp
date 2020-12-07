package com.mustafa.movieguideapp.repository.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.mustafa.movieguideapp.api.PeopleService
import com.mustafa.movieguideapp.models.*
import com.mustafa.movieguideapp.models.Resource.Error
import com.mustafa.movieguideapp.models.Resource.Success
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@OpenForTesting
@Singleton
class PeopleRepository @Inject constructor(
    private val service: PeopleService,
    private val peopleDao: PeopleDao,
) {

    fun loadPopularPeople(): Flowable<PagingData<Person>> {
        return Pager(
            config = PagingConfig(
                Constants.TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) { PeoplePagingSource(service) }
            .flowable
    }

    fun loadPersonDetail(id: Int): LiveData<Resource<PersonDetail>> {
        return LiveDataReactiveStreams.fromPublisher(service.fetchPersonDetail(id = id)
            .subscribeOn(Schedulers.io())
            .map<Resource<PersonDetail>> { Success(it, false) }
            .onErrorReturn { Error(it.toString()) }
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable()
        )

    }

    fun searchPeople(query: String): Flowable<PagingData<Person>> {
        return Pager(
            config = PagingConfig(
                Constants.TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            SearchPeoplePagingSource(service, query)
        }.flowable
    }

    fun loadMoviesForPerson(personId: Int): LiveData<Resource<List<MoviePerson>>> {
        return LiveDataReactiveStreams.fromPublisher(
            service.fetchPersonMovies(id = personId)
                .subscribeOn(Schedulers.io())
                .map<Resource<List<MoviePerson>>> { Success(it.cast, false) }
                .onErrorReturn { Error(it.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .toFlowable()
        )
    }

    fun loadTvsForPerson(personId: Int): LiveData<Resource<List<TvPerson>>> {
        return LiveDataReactiveStreams.fromPublisher(
            service.fetchPersonTvs(id = personId)
                .subscribeOn(Schedulers.io())
                .map<Resource<List<TvPerson>>> { Success(it.cast, false) }
                .onErrorReturn { Error(it.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .toFlowable()
        )
    }

    fun getPeopleSuggestions(query: String): LiveData<PagingData<Person>> {
        return LiveDataReactiveStreams.fromPublisher(
            Pager(
                config = PagingConfig(
                    Constants.TMDB_API_PAGE_SIZE,
                    enablePlaceholders = false
                )
            ) {
                SearchPeoplePagingSource(
                    service,
                    query = query
                )
            }.flowable
        )
    }

    suspend fun saveQuery(query: String) {
        peopleDao.insertQuery(PeopleRecentQueries(query))
    }

    suspend fun getPeopleRecentQueries(): List<String> {
        return peopleDao.getAllPeopleQueries()
    }

    suspend fun deleteAllPeopleRecentQueries() {
        peopleDao.deleteAllPeopleQueries()
    }
}
