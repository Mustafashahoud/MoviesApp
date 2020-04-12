package com.mustafa.movieapp.api.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.repository.DiscoverRepository
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockMovie
import com.mustafa.movieapp.view.ui.search.filter.MovieSearchFilterViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

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

        `when`(
            repository.loadFilteredMovies(
                8,
                "popularity.desc",
                null,
                null,
                null,
                null,
                null,
                null,
                1
            )
        ).thenReturn(filteredMoviesResultLiveData)

        viewModel.searchMovieListFilterLiveData.observeForever(observer)

        filteredMoviesResultLiveData.postValue(resourceData)

        viewModel.setFilters(
            8,
            "popularity.desc",
            null,
            null,
            null,
            null,
            null,
            null,
            1
        )
        verify(repository).loadFilteredMovies(
            8,
            "popularity.desc",
            null,
            null,
            null,
            null,
            null,
            null,
            1
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