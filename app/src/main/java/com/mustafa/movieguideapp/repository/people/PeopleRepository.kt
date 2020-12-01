package com.mustafa.movieguideapp.repository.people

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.rxjava2.flowable
import com.mustafa.movieguideapp.api.*
import com.mustafa.movieguideapp.models.*
import com.mustafa.movieguideapp.models.Resource.Error
import com.mustafa.movieguideapp.models.Resource.Success
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    suspend fun loadPersonDetail(id: Int): Flow<Resource<PersonDetail>> {
        return flow {
            service.fetchPersonDetail(id).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Success(data, false))
                    }
                }.onErrorSuspend {
                    emit(Error(message()))
                }.onExceptionSuspend {
                    emit(Error(message()))
                }
            }
        }
    }

    fun searchPeople(query: String): Flowable<PagingData<Person>> {
        return Pager(
            config = PagingConfig(
                Constants.TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            SearchPeoplePagingSource(
                service = service, peopleDao = peopleDao, query = query, search = true
            )
        }.flowable
    }

    suspend fun loadMoviesForPerson(personId: Int): LiveData<Resource<List<MoviePerson>>> {

        return LiveDataReactiveStreams.fromPublisher(
            service.fetchPersonMovies(id = personId)
                .subscribeOn(Schedulers.io())
                .map<Resource<List<MoviePerson>>> { Success(it.cast, false) }
                .onErrorReturn { Error(it.toString()) }
                .observeOn(AndroidSchedulers.mainThread())
                .toFlowable()
        )
    }

    suspend fun loadTvsForPerson(personId: Int): LiveData<Resource<List<TvPerson>>> {
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
                    peopleDao,
                    query = query,
                    search = false
                )
            }.flowable
        )
    }

    suspend fun getPeopleRecentQueries(): List<String> {
        return peopleDao.getAllPeopleQueries()
    }

    suspend fun deleteAllPeopleRecentQueries() {
        peopleDao.deleteAllPeopleQueries()
    }
}
