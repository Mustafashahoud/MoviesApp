package com.mustafa.movieapp.fragment

import android.os.SystemClock
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.movieapp.binding.FragmentBindingAdapters
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.util.DataBindingIdlingResourceRule
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListFragment
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@RunWith(AndroidJUnit4::class)
class MovieListFragmentTest {

//    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var viewModel: MovieListViewModel
    private val navController = mock<NavController>()
    private val results = MutableLiveData<Resource<List<Movie>>>()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()


    @Before
    fun init() {
        viewModel = mock()
        whenever(viewModel.movieListLiveData).thenReturn(results)



    }

    @Test
    fun testMovieLoaded_Integration() {
        val scenario = launchFragmentInContainer<MovieListFragment>()

    }

    @Ignore
    @Test
    fun testNavigationToSearchFragment() {
        launchFragmentInContainer<MovieListFragment>()
    }

    @Ignore
    @Test
    fun testNavigationToMovieDetailFragment() {
        launchFragmentInContainer<MovieListFragment>()
    }
}