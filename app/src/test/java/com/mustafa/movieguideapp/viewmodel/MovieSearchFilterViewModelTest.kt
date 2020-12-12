package com.mustafa.movieguideapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.models.FilterData
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockMovie
import com.mustafa.movieguideapp.view.ui.search.filter.MovieSearchFilterViewModel
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.anyInt

@RunWith(JUnit4::class)
class MovieSearchFilterViewModelTest {
    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var viewModel: MovieSearchFilterViewModel
    private val repository = mock<DiscoverRepository>()

    @Before
    fun init() {
        viewModel = MovieSearchFilterViewModel(repository)
    }

    @Test
    fun loadFilteredMoviesTest() {
        val observer = mock<Observer<Resource<List<Movie>>>>()
        val filteredMoviesResultLiveData = MutableLiveData<Resource<List<Movie>>>()
        val movie = mockMovie()
        val resourceData = Resource.success(listOf(movie), true)

        val filterData = FilterData()


        whenever(repository.loadFilteredMovies(any(), anyInt(), any())).thenReturn(
            filteredMoviesResultLiveData
        )


        viewModel.searchMovieListFilterLiveData.observeForever(observer)

        filteredMoviesResultLiveData.postValue(resourceData)

        viewModel.setFilters(
            filterData,
            1
        )

        verify(repository).loadFilteredMovies(
            any(),
            anyInt(),
            any()
        )

        verify(observer).onChanged(resourceData)

    }

    @Test
    fun loadFilteredMoviesNullTest() {
        val observer = mock<Observer<Resource<List<Movie>>>>()
        viewModel.searchMovieListFilterLiveData.observeForever(observer)
        viewModel.setPage(null)
        verifyNoMoreInteractions(repository)
    }
}