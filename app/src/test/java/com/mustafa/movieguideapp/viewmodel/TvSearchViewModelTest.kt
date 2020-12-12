package com.mustafa.movieguideapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockTv
import com.mustafa.movieguideapp.view.ui.search.TvSearchViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*


@RunWith(JUnit4::class)
class TvSearchViewModelTest {

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: TvSearchViewModel

    private val repository = mock<DiscoverRepository>()

    @Before
    fun init() {
        viewModel = TvSearchViewModel(repository)
    }

    @Test
    fun tvSuggestionsNullQueryTest() {
        val observer = mock<Observer<List<Tv>>>()
        viewModel.tvSuggestions.observeForever(observer)
        viewModel.setTvSuggestionsQuery(null)
        verifyNoMoreInteractions(repository)
        verify(repository, never()).getTvSuggestionsFromRoom(anyString())
    }

    @Test
    fun searchTvsTest() {
        val observer = mock<Observer<Resource<List<Tv>>>>()
        val searchTvsResultLiveData = MutableLiveData<Resource<List<Tv>>>()
        val searchTvs = Resource.success(listOf(mockTv()), true)

        `when`(repository.searchTvs(anyString(), anyInt())).thenReturn(
            searchTvsResultLiveData
        )
        viewModel.searchTvListLiveData.observeForever(observer)
        viewModel.setSearchTvQueryAndPage("query", 1)
        searchTvsResultLiveData.postValue(searchTvs)
        verify(repository).searchTvs("query", 1)
        verify(observer).onChanged(searchTvs)
    }

    @Test
    fun searchTvsNullQueryOrEmptyTest() {
        val observer = mock<Observer<Resource<List<Tv>>>>()
        viewModel.searchTvListLiveData.observeForever(observer)
        viewModel.setSearchTvQueryAndPage("", 1)
        verifyNoMoreInteractions(repository)
        verify(repository, never()).searchTvs(anyString(), anyInt())

        viewModel.setSearchTvQueryAndPage(null, 1)
        verifyNoMoreInteractions(repository)
        verify(repository, never()).searchTvs(anyString(), anyInt())

    }

    @Test
    fun tvSuggestionsTest() {
        val observer = mock<Observer<List<Tv>>>()
        val suggestionResultLiveData = MutableLiveData<List<Tv>>()
        val tvSuggestions = listOf(mockTv())

        `when`(repository.getTvSuggestionsFromRoom(anyString())).thenReturn(
            suggestionResultLiveData
        )
        viewModel.tvSuggestions.observeForever(observer)
        viewModel.setTvSuggestionsQuery("Tv")
        suggestionResultLiveData.postValue(tvSuggestions)
        verify(repository).getTvSuggestionsFromRoom("Tv")
        verify(observer).onChanged(tvSuggestions)
    }
}