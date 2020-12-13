package com.mustafa.movieguideapp.fragment.movie

import android.view.View
import android.widget.HorizontalScrollView
import android.widget.ListView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.entity.Movie
import com.mustafa.movieguideapp.util.*
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.view.ui.movies.moviedetail.MovieDetailFragment
import com.mustafa.movieguideapp.view.ui.movies.moviedetail.MovieDetailFragmentArgs
import com.mustafa.movieguideapp.view.ui.movies.moviedetail.MovieDetailViewModel
import com.nhaarman.mockitokotlin2.whenever
import com.schibsted.spain.barista.interaction.BaristaScrollInteractions.scrollTo
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.hamcrest.Matchers.anyOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class MovieDetailFragmentTest {

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
    private val keywordsLiveData = MutableLiveData<Resource<List<Keyword>>>()
    private val videosLiveData = MutableLiveData<Resource<List<Video>>>()
    private val reviewsLiveData = MutableLiveData<Resource<List<Review>>>()
    private lateinit var viewModel: MovieDetailViewModel

//    private lateinit var scenario: FragmentScenario<MovieDetailFragment>


    @Before
    fun init() {
        viewModel = mock(MovieDetailViewModel::class.java)

        whenever(viewModel.keywordListLiveData).thenReturn(keywordsLiveData)
        whenever(viewModel.reviewListLiveData).thenReturn(reviewsLiveData)
        whenever(viewModel.videoListLiveData).thenReturn(videosLiveData)

        //Two ways to create a bundle
        /*IMPORTANT NOTE*/
        /*The key must be the same as android:name in the argument Tag in your navGraph.*/
        //1- val bundle = bundleOf("movie" to Movie(1))

        // 2-
        val bundle = MovieDetailFragmentArgs(Movie(1)).toBundle()

        val scenario = launchFragmentInContainer(
            bundle, themeResId = R.style.AppTheme
        ) {
            MovieDetailFragment().apply {
                viewModelFactory = ViewModelUtil.createFor(viewModel)
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
    fun testMovieDetail() {
        // Given
        val keywords = MockTestUtil.mockKeywordList()
        val reviews = MockTestUtil.mockReviewList()
        val videos = MockTestUtil.mockVideoList()

        // Action
        keywordsLiveData.postValue(Resource.success(keywords, false))
        reviewsLiveData.postValue(Resource.success(reviews, false))
        videosLiveData.postValue(Resource.success(videos, false))

        // Assert ... Check

        //Trailers.. Videos
        onView(listMatcherVideos().atPosition(0)).check(matches(isDisplayed()))
        onView(listMatcherVideos().atPosition(0)).check(matches(hasDescendant(withText("video0"))))


        //Reviews
        // We need to scroll on the NastedScrollView to see the the fake Reviews.
        //onView(withId(R.id.NestedScrollView_movie_detail)).perform(NestedScrollViewExtension())
        scrollTo("Mustafa") // /* Mustafa is author of a fake review see MockTestUtil.mockReviewList()*/

        onView(listMatcherReviews().atPosition(0)).check(matches(isDisplayed()))
        onView(listMatcherReviews().atPosition(0)).check(matches(hasDescendant(withText("Mustafa"))))

        //Keywords
        onView(withId(R.id.detail_body_tags)).check(matches(isDisplayed()))

    }


    private fun listMatcherVideos(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.detail_body_recyclerView_trailers)
    }

    private fun listMatcherReviews(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.detail_body_recyclerView_reviews)
    }


    /**
     *  https://medium.com/@devasierra/espresso-nestedscrollview-scrolling-via-kotlin-delegation-5e7f0aa64c09
     *  Didn't work for me
     */
    @Suppress("unused")
    class NestedScrollViewExtension(scrolltoAction: ViewAction = ViewActions.scrollTo()) :
        ViewAction by scrolltoAction {
        override fun getConstraints(): Matcher<View> {
            return Matchers.allOf(
                withEffectiveVisibility(Visibility.VISIBLE),
                isDescendantOfA(
                    anyOf(
                        isAssignableFrom(NestedScrollView::class.java),
                        isAssignableFrom(ScrollView::class.java),
                        isAssignableFrom(HorizontalScrollView::class.java),
                        isAssignableFrom(ListView::class.java)
                    )
                )
            )
        }
    }
}