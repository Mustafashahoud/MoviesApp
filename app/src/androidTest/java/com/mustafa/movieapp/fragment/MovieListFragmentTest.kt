package com.mustafa.movieapp.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieListFragmentTest {

    @Test
    fun test() {
        val scenario = launchFragmentInContainer<MovieListFragment>()
    }
}