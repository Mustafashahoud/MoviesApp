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
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentBindingAdapters
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.search.MovieSearchViewModel
import com.mustafa.movieguideapp.view.ui.search.result.MovieSearchResultFragment
import com.mustafa.movieguideapp.view.ui.search.result.MovieSearchResultFragmentArgs
import com.nhaarman.mockitokotlin2.never
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

/**
 * @author Mustafa Shahoud
 * 4/19/2020
 */
@RunWith(AndroidJUnit4::class)
class MovieSearchResultFragmentTest {

    @Rule
    @JvmField
    val executorRule = TaskExecutorWithIdlingResourceRule()

    @Rule
    @JvmField
    val countingAppExecutors = CountingAppExecutorsRule()

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private lateinit var viewModel: MovieSearchViewModel

    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private val resultsLiveData = MutableLiveData<Resource<List<Movie>>>()

    @Before
    fun init() {
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)

        viewModel = mock(MovieSearchViewModel::class.java)

        `when`(viewModel.searchMovieListLiveData).thenReturn(resultsLiveData)

        val bundle = MovieSearchResultFragmentArgs("Troy").toBundle()
//        val bundle = bundleOf("query" to "Troy")

        val scenario = launchFragmentInContainer(
            bundle, themeResId = R.style.AppTheme
        ) {
            MovieSearchResultFragment().apply {
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

        runOnUiThread {
            navController.setGraph(R.navigation.movie)
        }

        /*THIS IS SO IMPORTANT To tel the navController that Here we are now */
        /*Otherwise the navController won't know and the nodeDis will null */
        runOnUiThread {
            navController.setCurrentDestination(R.id.movieSearchFragmentResult)
        }


        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun testError() {
        resultsLiveData.postValue(Resource.error("Something Unexpected happened", null))
        onView(withId(R.id.error_msg)).check(matches(isDisplayed()))
        onView(withId(R.id.error_msg)).check(matches(withText("Something Unexpected happened")))
    }

    @Test
    fun testLoadingWithoutData() {
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        resultsLiveData.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadingData() {
        resultsLiveData.postValue(
            Resource.success(
                listOf(
                    Movie(1, "Troy1"),
                    Movie(2, "Troy2")
                ),
                true
            )
        )
        onView(listMatcher().atPosition(0)).check(matches(isDisplayed()))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("Troy1"))))
        onView(listMatcher().atPosition(1)).check(matches(isDisplayed()))
        onView(listMatcher().atPosition(1)).check(matches(hasDescendant(withText("Troy2"))))

        // Test navigation
        onView(listMatcher().atPosition(0)).perform(click())
        assertThat(navController.currentDestination?.id, `is`(R.id.movieDetail))

    }

    @Test
    fun testScrollingWithNextPageTrue() {
        resultsLiveData.postValue(
            Resource.success(
                MockTestUtil.createMovies(20),
                true
            )
        )

        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_search_result_movies)).perform(action)
        onView(listMatcher().atPosition(19)).check(matches(isDisplayed()))
        Mockito.verify(viewModel).loadMore()
    }

    @Test
    fun testScrollingWithNextPageFalse() {
        resultsLiveData.postValue(
            Resource.success(
                MockTestUtil.createMovies(20),
                false
            )
        )

        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_search_result_movies)).perform(action)
        onView(listMatcher().atPosition(19)).check(matches(isDisplayed()))
        Mockito.verify(viewModel, never()).loadMore()
    }

    private fun listMatcher() = RecyclerViewMatcher(R.id.recyclerView_search_result_movies)


}