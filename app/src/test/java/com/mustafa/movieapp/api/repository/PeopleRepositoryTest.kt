
package com.mustafa.movieapp.api.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.mustafa.movieapp.api.PeopleService
import com.mustafa.movieapp.api.api.ApiUtil.successCall
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.models.network.PeopleResponse
import com.mustafa.movieapp.models.network.PersonDetail
import com.mustafa.movieapp.repository.PeopleRepository
import com.mustafa.movieapp.room.PeopleDao
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockPerson
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockPersonDetail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class PeopleRepositoryTest {

  private lateinit var repository: PeopleRepository
  private val peopleDao = mock<PeopleDao>()
  private val service = mock<PeopleService>()

  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()

  @Before
  fun init() {
    repository = PeopleRepository(service, peopleDao)
  }

  @Test
  fun loadPeopleFromNetwork() {
    val loadFromDB = MutableLiveData<List<Person>>()
    whenever(peopleDao.getPeople(1)).thenReturn(loadFromDB)

    val mockResponse = PeopleResponse(1, emptyList(), 100, 10)
    val call = successCall(mockResponse)
    whenever(service.fetchPopularPeople(1)).thenReturn(call)

    val data = repository.loadPeople(1)
    verify(peopleDao).getPeople(1)
    verifyNoMoreInteractions(service)

    val observer = mock<Observer<Resource<List<Person>>>>()
    data.observeForever(observer)
    verifyNoMoreInteractions(service)
    val updatedData = MutableLiveData<List<Person>>()
    whenever(peopleDao.getPeople(1)).thenReturn(updatedData)

    loadFromDB.postValue(null)
    verify(observer).onChanged(Resource.loading(null))
    verify(service).fetchPopularPeople(1)
    verify(peopleDao).insertPeople(mockResponse.results)

    updatedData.postValue(mockResponse.results)
    verify(observer).onChanged(Resource.success(mockResponse.results, false))
  }

  @Test
  fun loadPersonDetailFromNetwork() {
    val loadFromDB = mockPerson()
    whenever(peopleDao.getPerson(123)).thenReturn(loadFromDB)

    val mockResponse = mockPersonDetail()
    val call = successCall(mockResponse)
    whenever(service.fetchPersonDetail(123)).thenReturn(call)

    val data = repository.loadPersonDetail(123)
    verify(peopleDao).getPerson(123)
    verifyNoMoreInteractions(service)

    val observer = mock<Observer<Resource<PersonDetail>>>()
    data.observeForever(observer)
    verify(observer).onChanged(Resource.success(mockPersonDetail(), true))

    val updatedPerson = mockPerson()
    updatedPerson.personDetail = mockPersonDetail()
    verify(peopleDao).updatePerson(updatedPerson)
  }
}
