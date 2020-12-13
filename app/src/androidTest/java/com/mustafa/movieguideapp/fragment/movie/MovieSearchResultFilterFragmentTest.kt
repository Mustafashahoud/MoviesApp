package com.mustafa.movieguideapp.fragment.movie

import androidx.core.os.bundleOf
import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentBindingAdapters
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.LANGUAGES
import com.mustafa.movieguideapp.utils.FiltersConstants.Companion.RATINGS
import com.mustafa.movieguideapp.view.ui.search.filter.MovieSearchFilterViewModel
import com.mustafa.movieguideapp.view.ui.search.filter.MovieSearchResultFilterFragment
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`

/**
 * @author Mustafa Shahoud
 * 4/19/2020
 */
@RunWith(AndroidJUnit4::class)
class MovieSearchResultFilterFragmentTest {
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

    private lateinit var viewModel: MovieSearchFilterViewModel

    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private val resultsLiveData = MutableLiveData<Resource<List<Movie>>>()
    private val totalResultLiveData = MutableLiveData<String>()


    @Before
    fun init() {
        mockBindingAdapter = Mockito.mock(FragmentBindingAdapters::class.java)

        viewModel = Mockito.mock(MovieSearchFilterViewModel::class.java)

        `when`(viewModel.searchMovieListFilterLiveData).thenReturn(resultsLiveData)
        `when`(viewModel.totalMoviesCount).thenReturn(totalResultLiveData)

        // The arg here is a map of the name of the adapter to the filters that have been selected by the user
        val mapFilters: Map<String, List<String>> =
            hashMapOf(
                RATINGS to listOf("+9"),
                LANGUAGES to listOf("English")
            )

        val bundle = bundleOf("filters" to mapFilters)

        val scenario = launchFragmentInContainer(
            bundle, themeResId = R.style.AppTheme
        ) {
            MovieSearchResultFilterFragment().apply {
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
            navController.setCurrentDestination(R.id.movieSearchFragmentResultFilter)
        }


        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun testLoading() {
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_filters)).check(matches(isDisplayed()))
        resultsLiveData.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun testError() {
        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
        onView(withId(R.id.edit_filters)).check(matches(isDisplayed()))
        resultsLiveData.postValue(Resource.error("failed to load", null))
        onView(withId(R.id.error_msg)).check(matches(isDisplayed()))
    }

    @Test
    fun testLoadResults() {

        //Given
        resultsLiveData.postValue(
            Resource.success(
                listOf(
                    Movie(1, "Movie1"), Movie(1, "Movie2")
                ), true
            )
        )

        totalResultLiveData.postValue("2")

        onView(withId(R.id.total_filter_results)).check(matches(withText("2")))

        onView(listMatcher().atPosition(0)).check(
            matches(
                isDisplayed()
            )
        )
        onView(listMatcher().atPosition(0)).check(
            matches(hasDescendant(withText("Movie1")))
        )
        onView(listMatcher().atPosition(1)).check(
            matches(
                isDisplayed()
            )
        )
        onView(listMatcher().atPosition(1)).check(
            matches(hasDescendant(withText("Movie2")))
        )

        onView(listMatcher().atPosition(0)).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.movieDetail))
    }

    private fun listMatcher() = RecyclerViewMatcher(R.id.filtered_items_recycler_view)
}