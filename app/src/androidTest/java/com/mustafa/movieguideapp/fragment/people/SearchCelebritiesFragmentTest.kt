package com.mustafa.movieguideapp.fragment.people

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentBindingAdapters
import com.mustafa.movieguideapp.models.entity.PeopleRecentQueries
import com.mustafa.movieguideapp.models.entity.Person
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.person.search.SearchCelebritiesFragment
import com.mustafa.movieguideapp.view.ui.person.search.SearchCelebritiesResultViewModel
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SearchCelebritiesFragmentTest {

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

    private lateinit var viewModel: SearchCelebritiesResultViewModel

    private lateinit var mockBindingAdapter: FragmentBindingAdapters

    private val celebritiesSuggestionsLiveData = MutableLiveData<List<Person>>()
    private val celebritiesRecentQueries = MutableLiveData<List<PeopleRecentQueries>>()

    private val mockedPerson = MockTestUtil.mockPerson()


    @Before
    fun init() {
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)

        viewModel = mock(SearchCelebritiesResultViewModel::class.java)

        `when`(viewModel.peopleSuggestions).thenReturn(celebritiesSuggestionsLiveData)
        `when`(viewModel.getPeopleRecentQueries()).thenReturn(celebritiesRecentQueries)

        val scenario = launchFragmentInContainer(themeResId = R.style.AppTheme) {
            SearchCelebritiesFragment().apply {
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
            navController.setGraph(R.navigation.star)
        }

        /*THIS IS SO IMPORTANT To tel the navController that Here we are now */
        /*Otherwise the navController won't know and the nodeDis will null */
        runOnUiThread {
            navController.setCurrentDestination(R.id.searchCelebritiesFragment)
        }


        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun testSuggestion() {
        onView(withId(R.id.search_view))
            .perform(typeSearchViewText("M", false))

        celebritiesSuggestionsLiveData.postValue(listOf(mockedPerson))

        // Action
        onView(listMatcher().atPosition(0))
            .check(matches(isDisplayed()))
        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText(mockedPerson.name))))
    }

    @Test
    fun testPressingOnSuggestion() {
        onView(withId(R.id.search_view))
            .perform(typeSearchViewText("M", false))

        celebritiesSuggestionsLiveData.postValue(listOf(mockedPerson))

        // Action
        onView(listMatcher().atPosition(0))
            .check(matches(isDisplayed()))
        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText(mockedPerson.name))))
        onView(listMatcher().atPosition(0))
            .perform(click())

        assertThat(navController.currentDestination?.id, `is`(R.id.celebrityDetail))
    }

    @Test
    fun testRecentSearchesAndNavToSearchResultFragment() {

        celebritiesRecentQueries.postValue(listOf(PeopleRecentQueries("Mustafa")))

        // Check the list view displayed
        onView(withId(R.id.listView_recent_queries))
            .check(matches(isDisplayed()))

        onData(instanceOf(String::class.java))
            .inAdapterView(withId(R.id.listView_recent_queries))
            .atPosition(0).check(matches(isDisplayed()))

        onView(allOf(withText("Mustafa")))
            .perform(click())

        assertThat(
            navController.currentDestination?.id,
            `is`(R.id.searchCelebritiesResultFragment)
        )
    }

    @Test
    fun testClearDialogShowing() {

        //1//Given
        celebritiesRecentQueries.postValue(listOf(PeopleRecentQueries("Parasite")))

        //2//Action
        // Click on the Clear textView to CLear Recent Searches/Queries
        onView(withId(R.id.clear_recent_queries)).perform(click())

        //3//Assert/Check
        //Check the dialog with this text is displayed to the user
        onView(withText("Clear recent searches and pages?"))
            .inRoot(RootMatchers.isDialog())
            .check(matches(isDisplayed()))
        //Check the negative action that is CANCEL in my case
        onView(withId(android.R.id.button2))
            .inRoot(RootMatchers.isDialog())
            .check(matches(withText("CANCEL")))
            .check(matches(isDisplayed()))
        //Check the positive action that is CLEAR in my case
        onView(withId(android.R.id.button1))
            .inRoot(RootMatchers.isDialog())
            .check(matches(withText("CLEAR")))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testPressingClearOnTheDialog() {
        //1//Given
        celebritiesRecentQueries.postValue(listOf(PeopleRecentQueries("Mustafa")))
        //2//Action
        onView(withId(R.id.clear_recent_queries)).perform(click())
        onView(withId(android.R.id.button1))
            .inRoot(RootMatchers.isDialog()).perform(click())
        //3//Assert/Check that that listView of Queries has been deleted.
        onView(withId(R.id.listView_recent_queries))
            .check(matches(not(isDisplayed())))
    }


    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.recyclerView_suggestion)
    }

}