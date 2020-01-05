package com.mustafa.movieapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mustafa.movieapp.api.ApiResponse
import com.mustafa.movieapp.api.PeopleService
import com.mustafa.movieapp.mappers.PeoplePagingChecker
import com.mustafa.movieapp.mappers.PersonDetailPagingChecker
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.models.network.PeopleResponse
import com.mustafa.movieapp.models.network.PersonDetail
import com.mustafa.movieapp.room.PeopleDao
import com.mustafa.movieapp.view.ui.common.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PeopleRepository @Inject constructor(
        private val peopleService: PeopleService,
        private val peopleDao: PeopleDao,
        private val appExecutors: AppExecutors
) : Repository {

    fun loadPeople(page: Int): LiveData<Resource<List<Person>>> {
        return object : NetworkBoundResource<
                List<Person>,
                PeopleResponse,
                PeoplePagingChecker
        >(appExecutors) {
            override fun saveCallResult(items: PeopleResponse) {
                for (item in items.results) {
                    item.page = page
                }
                peopleDao.insertPeople(items.results)
            }

            override fun shouldFetch(data: List<Person>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Person>> {
                return peopleDao.getPeople(page_ = page)
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
}
