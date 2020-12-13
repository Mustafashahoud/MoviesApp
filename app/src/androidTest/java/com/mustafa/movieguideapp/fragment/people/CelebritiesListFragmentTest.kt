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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.binding.FragmentBindingAdapters
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.entity.Person
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.person.celebrities.CelebritiesListFragment
import com.mustafa.movieguideapp.view.ui.person.celebrities.CelebritiesListViewModel
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
class CelebritiesListFragmentTest {
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

    private lateinit var viewModel: CelebritiesListViewModel

    private val navController = TestNavHostController(
        ApplicationProvider.getApplicationContext()
    )
    private val results = MutableLiveData<Resource<List<Person>>>()

    @Before
    fun init() {
        viewModel = Mockito.mock(CelebritiesListViewModel::class.java)
        whenever(viewModel.peopleLiveData).thenReturn(results)

        mockBindingAdapter = Mockito.mock(FragmentBindingAdapters::class.java)

        val scenario = launchFragmentInContainer(
            themeResId = R.style.AppTheme
        ) {
            CelebritiesListFragment().apply {
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
            navController.setGraph(R.navigation.star)
        }

        scenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), navController)
            fragment.disableProgressBarAnimations()
        }
    }

    @Test
    fun testBasics_ProgressBar_ToolbarTitle() {
        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.toolbar_title)).check(
            matches(
                withText(
                    R.string.fragment_celebrities
                )
            )
        )
        results.postValue(Resource.loading(null))
        onView(withId(R.id.progress_bar))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loadResults() {
        val person = MockTestUtil.mockPerson()
        results.postValue(Resource.success(arrayListOf(person), true))

        onView(listMatcher().atPosition(0))
            .check(matches(isDisplayed()))

        //if your ViewHolder uses ViewGroup, wrap withText() with a hasDescendant() like:
        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText("MUSTAFA"))))

        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))

    }

    @Test
    fun dataWithLoading() {
        val person = MockTestUtil.mockPerson()
        results.postValue(Resource.loading(arrayListOf(person)))

        onView(listMatcher().atPosition(0))
            .check(matches(isDisplayed()))

        //if your ViewHolder uses ViewGroup, wrap withText() with a hasDescendant() like:
        onView(listMatcher().atPosition(0))
            .check(matches(hasDescendant(withText("MUSTAFA"))))

        onView(withId(R.id.progress_bar))
            .check(matches(not(isDisplayed())))

    }

    @Test
    fun error() {
        results.postValue(Resource.error("failed to load", null))
        onView(withId(R.id.error_msg)).check(matches(isDisplayed()))
    }

    @Test
    fun loadMore_HasNextPage_True() {
        val people = MockTestUtil.createPeople(20)
        results.postValue(Resource.success(people, true))
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_list_celebrities)).perform(action)
        onView(listMatcher().atPosition(19))
            .check(matches(isDisplayed()))
        verify(viewModel).loadMore()
    }

    @Test
    fun loadMore_HasNextPage_False() {
        val people = MockTestUtil.createPeople(20)
        results.postValue(Resource.success(people, false))
        val action = RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(19)
        onView(withId(R.id.recyclerView_list_celebrities)).perform(action)
        onView(listMatcher().atPosition(19)).check(matches(isDisplayed()))
        verify(viewModel, never()).loadMore()
    }

    @Test
    fun testNavigationToMovieSearchFragment() {
        onView(withId(R.id.search_icon)).perform(ViewActions.click())
        assertThat(
            navController.currentDestination?.id, `is`(R.id.searchCelebritiesFragment)
        )
    }

    @Test
    fun testNavigationToMovieDetailFragment() {
        //Given
        val person = MockTestUtil.mockPerson()
        results.postValue(Resource.success(arrayListOf(person), true))

        // Action
        onView(listMatcher().atPosition(0)).perform(ViewActions.click())

        // Assert
        assertThat(navController.currentDestination?.id, `is`(R.id.celebrityDetail))
    }

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.recyclerView_list_celebrities)
    }
}