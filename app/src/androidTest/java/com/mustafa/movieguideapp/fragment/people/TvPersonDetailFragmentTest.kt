package com.mustafa.movieguideapp.fragment.people

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.util.DataBindingIdlingResourceRule
import com.mustafa.movieguideapp.util.disableProgressBarAnimations
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.utils.StringUtils
import com.mustafa.movieguideapp.view.ui.person.detail.TvPersonDetailFragment
import com.mustafa.movieguideapp.view.ui.person.detail.TvPersonDetailFragmentArgs
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TvPersonDetailFragmentTest {

    @Rule
    @JvmField
    val dataBindingIdlingResourceRule = DataBindingIdlingResourceRule()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    private val tvPerson = MockTestUtil.mockTvPerson()

    @Before
    fun init() {
        val bundle = TvPersonDetailFragmentArgs(tvPerson).toBundle()

        val scenario = launchFragmentInContainer(
            bundle, themeResId = R.style.AppTheme
        ) {
            TvPersonDetailFragment()
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
    fun testTvPersonDetail() {
        onView(withId(R.id.tv_celebrity_detail_poster)).check(
            matches(
                isDisplayed()
            )
        )
        onView(withId(R.id.toolbar_title)).check(
            matches(
                withText(tvPerson.name)
            )
        )
        onView(withId(R.id.detail_body_summary)).check(
            matches(
                withText(tvPerson.overview)
            )
        )

        onView(withId(R.id.detail_header_title)).check(
            matches(
                withText(tvPerson.name)
            )
        )

        onView(withId(R.id.detail_header_celebrity_character)).check(
            matches(
                withText("Ch.: ${tvPerson.character}")
            )
        )

        onView(withId(R.id.summary_title)).check(
            matches(
                withText("Summary")
            )
        )

        onView(withId(R.id.detail_header_release)).check(
            matches(
                withText("First Air Date: ${tvPerson.first_air_date}")
            )
        )
        onView(withId(R.id.detail_header_genre)).check(
            matches(
                withText("Genre: ${StringUtils.getTvGenresById(tvPerson.genre_ids)}")
            )
        )
    }
}