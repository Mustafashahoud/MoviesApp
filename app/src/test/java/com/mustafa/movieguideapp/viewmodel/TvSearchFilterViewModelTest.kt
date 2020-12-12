package com.mustafa.movieguideapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockTv
import com.mustafa.movieguideapp.view.ui.search.filter.TvSearchFilterViewModel
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.`when`

@RunWith(JUnit4::class)
class TvSearchFilterViewModelTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var viewModel: TvSearchFilterViewModel
    private val repository = mock<DiscoverRepository>()

    @Before
    fun init() {
        viewModel = TvSearchFilterViewModel(repository)
    }

    @Test
    fun loadFilteredTvsTest() {
        val observer = mock<Observer<Resource<List<Tv>>>>()
        val filteredTvsResultLiveData = MutableLiveData<Resource<List<Tv>>>()
        val tv = mockTv()
        val resourceData = Resource.success(listOf(tv), true)

        `when`(
            repository.loadFilteredTvs(
                any(),
                anyInt(),
                any()
            )
        ).thenReturn(filteredTvsResultLiveData)

        viewModel.searchTvListFilterLiveData.observeForever(observer)

        filteredTvsResultLiveData.postValue(resourceData)

        val filterData = FilterData()


        viewModel.setFilters(filterData, 1)
        verify(repository).loadFilteredTvs(
            any(),
            anyInt(),
            any()
        )

        verify(observer, times(1)).onChanged(resourceData)

    }

    @Test
    fun loadFilteredTvsNullTest() {
        val observer = mock<Observer<Resource<List<Tv>>>>()
        viewModel.searchTvListFilterLiveData.observeForever(observer)
        viewModel.setPage(null)
        verifyNoMoreInteractions(repository)
    }
}