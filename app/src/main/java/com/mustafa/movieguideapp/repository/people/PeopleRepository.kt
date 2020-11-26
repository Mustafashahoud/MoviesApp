package com.mustafa.movieguideapp.repository.people

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mustafa.movieguideapp.api.*
import com.mustafa.movieguideapp.models.*
import com.mustafa.movieguideapp.models.Resource.Error
import com.mustafa.movieguideapp.models.Resource.Success
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.utils.Constants
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

    fun loadPopularPeople(): Flow<PagingData<Person>> {
        return Pager(
            config = PagingConfig(
                Constants.TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) { PeoplePagingSource(service) }.flow
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

    fun searchPeople(query: String): Flow<PagingData<Person>> {
        return Pager(
            config = PagingConfig(
                Constants.TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            SearchPeoplePagingSource(
                service = service, peopleDao = peopleDao, query = query, search = true
            )
        }.flow
    }

    suspend fun loadMoviesForPerson(personId: Int): Flow<Resource<List<MoviePerson>>> {
        return flow {
            service.fetchPersonMovies(id = personId).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Success(data.cast, false))
                    }
                }.onErrorSuspend {
                    emit(Error(message()))
                }.onExceptionSuspend {
                    emit(Error(message()))
                }
            }
        }
    }

    suspend fun loadTvsForPerson(personId: Int): Flow<Resource<List<TvPerson>>> {
        return flow {
            service.fetchPersonTvs(id = personId).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Success(data.cast, false))
                    }
                }.onErrorSuspend {
                    emit(Error(message()))
                }.onExceptionSuspend {
                    emit(Error(message()))
                }
            }
        }
    }

    fun getPeopleSuggestions(query: String): Flow<PagingData<Person>> {
        return Pager(
            config = PagingConfig(
                Constants.TMDB_API_PAGE_SIZE,
                enablePlaceholders = false
            )
        ) {
            SearchPeoplePagingSource(
                service = service,
                query = query,
                search = false
            )
        }.flow
    }

    suspend fun getPeopleRecentQueries(): List<String> {
        return peopleDao.getAllPeopleQueries()
    }

    suspend fun deleteAllPeopleRecentQueries() {
        peopleDao.deleteAllPeopleQueries()
    }
}
