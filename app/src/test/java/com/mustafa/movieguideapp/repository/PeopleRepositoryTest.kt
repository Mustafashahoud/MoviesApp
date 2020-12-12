package com.mustafa.movieguideapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.api.PeopleService
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.PeopleResult
import com.mustafa.movieguideapp.models.entity.Person
import com.mustafa.movieguideapp.models.network.PeopleResponse
import com.mustafa.movieguideapp.models.network.PersonDetail
import com.mustafa.movieguideapp.room.AppDatabase
import com.mustafa.movieguideapp.room.PeopleDao
import com.mustafa.movieguideapp.util.ApiUtil.successCall
import com.mustafa.movieguideapp.util.InstantAppExecutors
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockPerson
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockPersonDetail
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyList
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class PeopleRepositoryTest {

    private lateinit var repository: PeopleRepository
    private val peopleDao = mock<PeopleDao>()
    private val service = mock<PeopleService>()
    private val db = mock<AppDatabase>()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        repository = PeopleRepository(service, peopleDao, db, InstantAppExecutors())
    }

    @Test
    fun loadPeopleTest() {
        val ids = arrayListOf(1, 2)
        val peopleResult = PeopleResult(ids, 1)
        val peopleResultLive = MutableLiveData<PeopleResult>()
        val mockResponse = PeopleResponse(1, emptyList(), 100, 10)
        val callLiveData = successCall(mockResponse)
        val observer = mock<Observer<Resource<List<Person>>>>()
        val people = MutableLiveData<List<Person>>()
        val persons = listOf(mockPerson())

        `when`(service.fetchPopularPeople(1)).thenReturn(callLiveData)
        `when`(peopleDao.getPeopleResultByPageLiveData(1)).thenReturn(peopleResultLive)
        `when`(peopleDao.loadPeopleListOrdered(ids)).thenReturn(people)

        repository.loadPeople(1).observeForever(observer)
        verify(observer).onChanged(Resource.loading(null))

        peopleResultLive.postValue(null)
        verify(peopleDao, never()).loadPeopleListOrdered(anyList())
        verify(service).fetchPopularPeople(1)

        peopleResultLive.postValue(peopleResult)
        verify(peopleDao).loadPeopleListOrdered(ids)

        people.postValue(persons)
        Mockito.verify(observer).onChanged(Resource.success(persons, true))
        Mockito.verifyNoMoreInteractions(service)

    }

    @Test
    fun loadPersonDetailTest() {
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
        verify(observer).onChanged(Resource.success(mockPersonDetail(), false))

        val updatedPerson = mockPerson()
        updatedPerson.personDetail = mockPersonDetail()
        verify(peopleDao).updatePerson(updatedPerson)
    }
}
