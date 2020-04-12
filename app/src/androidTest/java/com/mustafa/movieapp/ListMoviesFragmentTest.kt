package com.mustafa.movieapp


import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.mustafa.movieapp.binding.FragmentBindingAdapters
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.testing.SingleFragmentActivity
import com.mustafa.movieapp.util.CountingAppExecutorsRule
import com.mustafa.movieapp.util.DataBindingIdlingResourceRule
import com.mustafa.movieapp.util.TaskExecutorWithIdlingResourceRule
import com.mustafa.movieapp.util.mock
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListFragment
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListViewModel
import org.junit.Rule
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ListMoviesFragmentTest {

    @Rule
    @JvmField
    val activityRule = ActivityTestRule(SingleFragmentActivity::class.java, true, true)
    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()
    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()
    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule(activityRule)

    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var viewModel: MovieListViewModel
    private val results = MutableLiveData<Resource<List<Movie>>>()
    private val searchFragment = TestListMoviesFragment()





    class TestListMoviesFragment : MovieListFragment() {
        val navController = mock<NavController>()
        override fun navController() = navController
    }
}