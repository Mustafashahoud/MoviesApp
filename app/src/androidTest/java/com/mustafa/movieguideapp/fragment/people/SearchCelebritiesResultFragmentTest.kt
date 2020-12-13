package com.mustafa.movieguideapp.fragment.people

import androidx.databinding.DataBindingComponent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentBindingAdapters
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Person
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.person.search.SearchCelebritiesResultFragment
import com.mustafa.movieguideapp.view.ui.person.search.SearchCelebritiesResultFragmentArgs
import com.mustafa.movieguideapp.view.ui.person.search.SearchCelebritiesResultViewModel
import com.nhaarman.mockitokotlin2.never
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class SearchCelebritiesResultFragmentTest {
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

    private val resultsLiveData = MutableLiveData<Resource<List<Person>>>()

    @Before
    fun init() {
        mockBindingAdapter = mock(FragmentBindingAdapters::class.java)

        viewModel = mock(SearchCelebritiesResultViewModel::class.java)

        `when`(viewModel.searchPeopleListLiveData).thenReturn(resultsLiveData)

        val bundle = SearchCelebritiesResultFragmentArgs("Mustafa").toBundle()

        val scenario = launchFragmentInContainer(
            bundle, themeResId = R.style.AppTheme
        ) {
            SearchCelebritiesResultFragment().apply {
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
            navController.setCurrentDestination(R.id.searchCelebritiesResultFragment)
        }


        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @Test
    fun testError() {
        resultsLiveData.postValue(Resource.error("Something Unexpected happened", null))
        onView(withId(R.id.error_msg))
            .check(matches(isDisplayed()))
        onView(withId(R.id.error_msg))
            .check(matches(withText("Something Unexpected happened")))
    }

    @Test
    fun testLoadingWithoutData() {
        onView(withId(R.id.progress_bar))
            .check(matches(Matchers.not(isDisplayed())))
        resultsLiveData.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testLoadingData() {
        val celebrity1 = MockTestUtil.mockPerson()
        val celebrity2 = MockTestUtil.mockPerson()

        resultsLiveData.postValue(
            Resource.success(
                listOf(
                    celebrity1,
                    celebrity2
                ),
                true
            )
        )
        onView(listMatcher().atPosition(0))
            .check(matches(isDisplayed()))
        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText(celebrity1.name))))
        onView(listMatcher().atPosition(1))
            .check(matches(isDisplayed()))
        onView(listMatcher().atPosition(1))
            .check(matches(hasDescendant(withText(celebrity2.name))))

        // Test navigation
        onView(listMatcher().atPosition(0)).perform(ViewActions.click())
        assertThat(
            navController.currentDestination?.id,
            Matchers.`is`(R.id.celebrityDetail)
        )

    }

    @Test
    fun testScrollingWithNextPageTrue() {
        resultsLiveData.postValue(
            Resource.success(
                MockTestUtil.createPeople(20),
                true
            )
        )

        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_search_result_people)).perform(action)
        onView(listMatcher().atPosition(19))
            .check(matches(isDisplayed()))
        verify(viewModel).loadMore()
    }

    @Test
    fun testScrollingWithNextPageFalse() {
        resultsLiveData.postValue(
            Resource.success(
                MockTestUtil.createPeople(20),
                false
            )
        )

        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_search_result_people)).perform(action)
        onView(listMatcher().atPosition(19))
            .check(matches(isDisplayed()))
        verify(viewModel, never()).loadMore()
    }

    private fun listMatcher() = RecyclerViewMatcher(R.id.recyclerView_search_result_people)
}