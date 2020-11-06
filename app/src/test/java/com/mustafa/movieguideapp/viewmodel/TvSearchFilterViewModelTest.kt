package com.mustafa.movieguideapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockTv
import com.mustafa.movieguideapp.view.ui.search.filter.TvSearchFilterViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

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
                nullable(Int::class.java),
                nullable(String::class.java),
                nullable(Int::class.java),
                nullable(String::class.java),
                nullable(String::class.java),
                nullable(String::class.java),
                nullable(Int::class.java),
                anyInt()
            )
        ).thenReturn(filteredTvsResultLiveData)

        viewModel.searchTvListFilterLiveData.observeForever(observer)

        filteredTvsResultLiveData.postValue(resourceData)

        viewModel.setFilters(page = 1)
        verify(repository).loadFilteredTvs(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            1
        )

        viewModel.setFilters(
            8,
            "popularity.desc",
            null,
            null,
            null,
            "EN",
            null,
            1
        )

        verify(repository).loadFilteredTvs(
            8,
            "popularity.desc",
            null,
            null,
            null,
            "EN",
            null,
            1
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