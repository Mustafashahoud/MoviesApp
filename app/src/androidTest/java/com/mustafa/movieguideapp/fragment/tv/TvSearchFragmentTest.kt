package com.mustafa.movieguideapp.fragment.tv

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentBindingAdapters
import com.mustafa.movieguideapp.models.entity.Tv
import com.mustafa.movieguideapp.models.entity.TvRecentQueries
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.view.ui.search.TvSearchFragment
import com.mustafa.movieguideapp.view.ui.search.TvSearchViewModel
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock


/**
 * @author Mustafa Shahoud
 * 4/18/2020
 */
@RunWith(AndroidJUnit4::class)
class TvSearchFragmentTest {

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

    private lateinit var viewModel: TvSearchViewModel

    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private val tvSuggestions = MutableLiveData<List<Tv>>()
    private val tvRecentQueries = MutableLiveData<List<TvRecentQueries>>()


    @Before
    fun init() {
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)

        viewModel = mock(TvSearchViewModel::class.java)

        `when`(viewModel.tvSuggestions).thenReturn(tvSuggestions)
        `when`(viewModel.getTvRecentQueries()).thenReturn(tvRecentQueries)

        val scenario = launchFragmentInContainer(themeResId = R.style.AppTheme) {
            TvSearchFragment().apply {
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
            navController.setGraph(R.navigation.tv)
        }

        /*THIS IS SO IMPORTANT To tel the navController that Here we are now */
        /*Otherwise the navController won't know and the nodeDis will null */
        runOnUiThread {
            navController.setCurrentDestination(R.id.tvSearchFragment)
        }


        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun testSuggestions() {
        onView(withId(R.id.search_view))
            .perform(typeSearchViewText("V", false))

        tvSuggestions.postValue(listOf(Tv(1, name = "Vikings")))

        // Action
        onView(listMatcher().atPosition(0)).check(matches(isDisplayed()))
        onView(listMatcher().atPosition(0)).check(matches(hasDescendant(withText("Vikings"))))
    }

    @Test
    fun testRecentSearchesAndNavToSearchResultFragment() {

        tvRecentQueries.postValue(listOf(TvRecentQueries("Vikings")))

        // Check the list view displayed
        onView(withId(R.id.listView_recent_queries)).check(matches(isDisplayed()))

        //onData(anyThing())
        onData(instanceOf(String::class.java))
            .inAdapterView(withId(R.id.listView_recent_queries))
            .atPosition(0).check(matches(isDisplayed()))

        onView(allOf(withText("Vikings"))).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.tvSearchFragmentResult))
    }

    @Test
    fun testClearDialogShowing() {

        //1//Given
        tvRecentQueries.postValue(listOf(TvRecentQueries("Vikings")))

        //2//Action
        // Click on the Clear textView to CLear Recent Searches/Queries
        onView(withId(R.id.clear_recent_queries)).perform(click())

        //3//Assert/Check

        //Check the dialog with this text is displayed to the user
        onView(withText("Clear recent searches and pages?")).inRoot(isDialog())
            .check(matches(isDisplayed()))
        //Check the negative action that is CANCEL in my case
        onView(withId(android.R.id.button2)).inRoot(isDialog()).check(matches(withText("CANCEL")))
            .check(matches(isDisplayed()))
        //Check the positive action that is CLEAR in my case
        onView(withId(android.R.id.button1)).inRoot(isDialog()).check(matches(withText("CLEAR")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPressingClearOnTheDialog() {
        //1//Given
        tvRecentQueries.postValue(listOf(TvRecentQueries("Parasite")))
        //2//Action
        onView(withId(R.id.clear_recent_queries)).perform(click())
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
        //3//Assert/Check that that listView of Queries has been deleted.
        onView(withId(R.id.listView_recent_queries)).check(matches(not(isDisplayed())))
    }

    @Test
    fun testPressingOnFilterTab_AllFiltersAreDisplayed() {

        // Select tabs Filter your Movies
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1))

        closeSoftKeyboard()

        onView(withId(R.id.ratings_title)).check(matches(isDisplayed()))
        onView(withId(R.id.genre_title)).check(matches(isDisplayed()))
        onView(withId(R.id.keyword_title)).check(matches(isDisplayed()))
        onView(withId(R.id.year_title)).check(matches(isDisplayed()))
        onView(withId(R.id.runtime_title)).check(matches(isDisplayed()))
        onView(withId(R.id.language_title)).check(matches(isDisplayed()))

        // Click the first button on all filters(RecyclerViews horizontally)
        onView(RecyclerViewMatcher(R.id.recycler_view_ratings).atPosition(0)).check(
            matches(
                isDisplayed()
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_genres).atPosition(0)).check(
            matches(
                isDisplayed()
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_years).atPosition(0)).check(
            matches(
                isDisplayed()
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_keywords).atPosition(0)).check(
            matches(
                isDisplayed()
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_runtimes).atPosition(0)).check(
            matches(
                isDisplayed()
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_languages).atPosition(0)).check(
            matches(
                isDisplayed()
            )
        )

        onView(RecyclerViewMatcher(R.id.recycler_view_ratings).atPosition(0)).check(
            matches(
                withText(
                    "+9"
                )
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_genres).atPosition(0)).check(
            matches(
                withText(
                    "Adventure"
                )
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_years).atPosition(0)).check(
            matches(
                withText("2020")
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_keywords).atPosition(0)).check(
            matches(
                withText("Anim")
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_runtimes).atPosition(0)).check(
            matches(
                withText("1 hour or more")
            )
        )
        onView(RecyclerViewMatcher(R.id.recycler_view_languages).atPosition(0)).check(
            matches(
                withText("English")
            )
        )

        onView(withId(R.id.see_result)).check(matches(isDisplayed()))
        onView(
            allOf(
                withId(R.id.clear_filter),
                withText(R.string.clear)
            )
        ).check(matches(isDisplayed()))
    }


    @Test
    fun testSelectFiltersAndPressingSeeResult() {

        // Select tabs Filter your Movies
        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1))

        closeSoftKeyboard()

        // Click the first button on all filters(RecyclerViews horizontally)
        onView(RecyclerViewMatcher(R.id.recycler_view_ratings).atPosition(0)).perform(click())
        onView(RecyclerViewMatcher(R.id.recycler_view_genres).atPosition(0)).perform(click())
        onView(RecyclerViewMatcher(R.id.recycler_view_years).atPosition(0)).perform(click())
        onView(RecyclerViewMatcher(R.id.recycler_view_keywords).atPosition(0)).perform(click())
        onView(RecyclerViewMatcher(R.id.recycler_view_runtimes).atPosition(0)).perform(click())
        onView(RecyclerViewMatcher(R.id.recycler_view_languages).atPosition(0)).perform(click())

        onView(withId(R.id.see_result)).perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.tvSearchFragmentResultFilter))
    }
//
//    @Test
//    fun testFilterButtonsColors() {
//        // Select tabs Filter your Movies
//        onView(withId(R.id.tabs)).perform(selectTabAtPosition(1))
//
//        closeSoftKeyboard()
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_ratings).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_genres).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_keywords).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_runtimes).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_years).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_languages).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//
//        onView(RecyclerViewMatcher(R.id.recycler_view_ratings).atPosition(0)).perform(click())
//        onView(RecyclerViewMatcher(R.id.recycler_view_genres).atPosition(0)).perform(click())
//        onView(RecyclerViewMatcher(R.id.recycler_view_years).atPosition(0)).perform(click())
//        onView(RecyclerViewMatcher(R.id.recycler_view_keywords).atPosition(0)).perform(click())
//        onView(RecyclerViewMatcher(R.id.recycler_view_runtimes).atPosition(0)).perform(click())
//        onView(RecyclerViewMatcher(R.id.recycler_view_languages).atPosition(0)).perform(click())
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_ratings).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.colorAccent)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_genres).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.colorAccent)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_keywords).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.colorAccent)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_runtimes).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.colorAccent)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_years).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.colorAccent)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_languages).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.colorAccent)))
//
//
//        // Clear filter the color on filters should be cleared
//        onView(withId(R.id.clear_filter)).perform(click())
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_ratings).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_genres).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_keywords).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_runtimes).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_years).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//        onView(
//            RecyclerViewMatcher(R.id.recycler_view_languages).atPosition(0)
//        ).check(matches(matchesBackgroundColor(R.color.itemsColor)))
//
//    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.recyclerView_suggestion)
    }

}