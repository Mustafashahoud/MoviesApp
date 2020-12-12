package com.mustafa.movieguideapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.tv.tvlist.TvListViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class TvListViewModelTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var viewModel : TvListViewModel
    private val repository = mock<DiscoverRepository>()

    @Before
    fun init() {
        viewModel = TvListViewModel(repository)
    }

    @Test
    fun loadMoreTest() {
        val observer = mock<Observer<Resource<List<Tv>>>>()
        viewModel.tvListLiveData.observeForever(observer)
        viewModel.loadMore()
        verify(repository).loadTvs(2)
    }

    @Test
    fun basicLoadTvsTest() {
        val observer = mock<Observer<Resource<List<Tv>>>>()
        viewModel.tvListLiveData.observeForever(observer)
        viewModel.setTvPage(1)
        // Cuz there is init block changing the pageNumber
        verify(repository, times(2)).loadTvs(1)

        viewModel.setTvPage(2)
        verify(repository).loadTvs(2)
        verify(repository, never()).loadTvs(3)
    }


    @Test
    fun loadTvsTest() {
        val listTvsLiveData = MutableLiveData<Resource<List<Tv>>>()
        val observer = mock<Observer<Resource<List<Tv>>>>()
        val tv = MockTestUtil.mockTv()
        val resourceData = Resource.success(listOf(tv), true)

        `when`(repository.loadTvs(2)).thenReturn(listTvsLiveData)

        viewModel.tvListLiveData.observeForever(observer)

        viewModel.setTvPage(2)
        listTvsLiveData.postValue(resourceData)

        verify(repository).loadTvs(1)
        verify(repository).loadTvs(2)
        verify(observer).onChanged(resourceData)
    }
}