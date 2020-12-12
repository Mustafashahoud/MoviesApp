package com.mustafa.movieguideapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.repository.DiscoverRepository
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.movies.movielist.MovieListViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*

@RunWith(JUnit4::class)
class MovieListViewModelTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var viewModel : MovieListViewModel
    private val repository = mock<DiscoverRepository>()

    @Before
    fun init() {
        viewModel = MovieListViewModel(repository)
    }

    @Test
    fun loadMoreTest() {
        val observer = mock<Observer<Resource<List<Movie>>>>()
        viewModel.movieListLiveData.observeForever(observer)
        viewModel.loadMore()
        verify(repository).loadMovies(2)
    }

    @Test
    fun basicLoadMoviesTest() {
        val observer = mock<Observer<Resource<List<Movie>>>>()
        viewModel.movieListLiveData.observeForever(observer)
        viewModel.setMoviePage(1)
        // Cuz there is init block changing the pageNumber
        verify(repository, times(2)).loadMovies(1)

        viewModel.setMoviePage(2)
        verify(repository).loadMovies(2)
        verify(repository, never()).loadMovies(3)
    }


    @Test
    fun loadMoviesTest() {
        val listMoviesLiveData = MutableLiveData<Resource<List<Movie>>>()
        val observer = mock<Observer<Resource<List<Movie>>>>()
        val movie = MockTestUtil.mockMovie()
        val resourceData = Resource.success(listOf(movie), true)

        `when`(repository.loadMovies(2)).thenReturn(listMoviesLiveData)

        viewModel.movieListLiveData.observeForever(observer)

        viewModel.setMoviePage(2)
        listMoviesLiveData.postValue(resourceData)

        verify(repository).loadMovies(1)
        verify(repository).loadMovies(2)
        verify(observer).onChanged(resourceData)
    }
}