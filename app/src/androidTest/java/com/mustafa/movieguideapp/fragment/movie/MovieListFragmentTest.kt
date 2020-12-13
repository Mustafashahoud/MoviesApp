package com.mustafa.movieguideapp.fragment.movie

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentBindingAdapters
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.movies.movielist.MovieListFragment
import com.mustafa.movieguideapp.view.ui.movies.movielist.MovieListViewModel
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify


/**
 *  https://developer.android.com/guide/navigation/navigation-testing
 */
@RunWith(AndroidJUnit4::class)
class MovieListFragmentTest {

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()

    private lateinit var mockBindingAdapter: FragmentBindingAdapters
    private lateinit var viewModel: MovieListViewModel
    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )
    private val results = MutableLiveData<Resource<List<Movie>>>()

    @Before
    fun init() {
        viewModel = mock(MovieListViewModel::class.java)
        whenever(viewModel.movieListLiveData).thenReturn(results)

        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)

        val scenario = launchFragmentInContainer(themeResId = R.style.AppTheme) {
            MovieListFragment().apply {
                appExecutors = countingAppExecutors.appExecutors
                viewModelFactory = ViewModelUtil.createFor(viewModel)
                dataBindingComponent = object : DataBindingComponent {
                    override fun getFragmentBindingAdapters(): FragmentBindingAdapters {
                        return mockBindingAdapter
                    }
                }
            }
        }
        dataBindingIdlingResourceRule.monitorFragment(scenario)

        // Set the navigation graph to the NavHostController
        runOnUiThread {
            navController.setGraph(R.navigation.movie)
        }
            scenario.onFragment { fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController)
                fragment.disableProgressBarAnimations()
            }
        }

    @Test
    fun testBasics_ProgressBar_ToolbarTitle() {
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.toolbar_title)).check(
            matches(
                withText(
                    "Movies"
                )
            )
        )
        results.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun loadResults() {
        val movie = Movie(id = 1, title = "MUSTAFA")
        results.postValue(Resource.success(arrayListOf(movie), true))

        onView(listMatcher().atPosition(0)).check(matches(isDisplayed()))

        //if your ViewHolder uses ViewGroup, wrap withText() with a hasDescendant() like:
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("MUSTAFA"))))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))

    }

    @Test
    fun dataWithLoading() {
        val movie = Movie(id = 1, title = "MUSTAFA")
        results.postValue(Resource.loading(arrayListOf(movie)))

        onView(listMatcher().atPosition(0)).check(matches(isDisplayed()))

        //if your ViewHolder uses ViewGroup, wrap withText() with a hasDescendant() like:
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("MUSTAFA"))))

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))

    }

    @Test
    fun error() {
        results.postValue(Resource.error("failed to load", null))
        onView(withId(R.id.error_msg)).check(matches(isDisplayed()))
    }

    @Test
    fun loadMore_HasNextPage_True() {
        val movies = MockTestUtil.createMovies(20)
        results.postValue(Resource.success(movies, true))
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_list_movies)).perform(action)
        onView(listMatcher().atPosition(19)).check(matches(isDisplayed()))
        verify(viewModel).loadMore()
    }

    @Test
    fun loadMore_HasNextPage_False() {
        val movies = MockTestUtil.createMovies(20)
        results.postValue(Resource.success(movies, false))
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_list_movies)).perform(action)
        onView(listMatcher().atPosition(19)).check(matches(isDisplayed()))
        verify(viewModel, never()).loadMore()
    }

    @Test
    fun testNavigationToMovieSearchFragment() {
        onView(withId(R.id.search_icon)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.movieSearchFragment))
    }

    @Test
    fun testNavigationToMovieDetailFragment() {
        //Given
        val movie = Movie(id = 1, title = "MUSTAFA")
        results.postValue(Resource.success(arrayListOf(movie), true))

        // Action
        onView(listMatcher().atPosition(0)).perform(click())

        // Assert
        assertThat(navController.currentDestination?.id, `is`(R.id.movieDetail))
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.recyclerView_list_movies)
    }
}