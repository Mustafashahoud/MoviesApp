package com.mustafa.movieapp.api.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Person
import com.mustafa.movieapp.repository.PeopleRepository
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockPerson
import com.mustafa.movieapp.view.ui.person.celebrities.CelebritiesListViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyInt

class CelebritiesListViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CelebritiesListViewModel
    private val repository = mock<PeopleRepository>()

    @Before
    fun init() {
        viewModel = CelebritiesListViewModel(repository)
    }


    @Test
    fun testLoadPeople() {
        val observer = mock<Observer<Resource<List<Person>>>>()
        val person = mockPerson()
        val resourceData = Resource.success(listOf(person), true)
        val peopleLiveData = MutableLiveData<Resource<List<Person>>>()
        `when`(repository.loadPeople(anyInt())).thenReturn(peopleLiveData)
        viewModel.peopleLiveData.observeForever(observer)
        peopleLiveData.postValue(resourceData)
        verify(repository).loadPeople(1)
        verify(observer).onChanged(resourceData)
    }

}