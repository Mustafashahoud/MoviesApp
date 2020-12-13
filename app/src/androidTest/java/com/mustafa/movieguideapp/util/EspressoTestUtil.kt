package com.mustafa.movieguideapp.util

import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import org.hamcrest.CoreMatchers
import org.hamcrest.Description
import org.hamcrest.Matcher
import timber.log.Timber



/**
 * Copied form https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample
 */
/**
 * Disables progress bar animations for all views in the fragment
 */
fun Fragment.disableProgressBarAnimations() {
    // traverse all views, if any is a progress bar, replace its animation
    traverseViews(requireView())
}

private fun traverseViews(view: View?) {
    if (view is ViewGroup) {
        traverseViewGroup(view)
    } else if (view is ProgressBar) {
        disableProgressBarAnimation(view)
    }
}

private fun traverseViewGroup(view: ViewGroup) {
    val count = view.childCount
    (0 until count).forEach {
        traverseViews(view.getChildAt(it))
    }
}

/**
 * necessary to run tests on older API levels where progress bar uses handler loop to animate.
 *
 * @param progressBar The progress bar whose animation will be swapped with a drawable
 */
private fun disableProgressBarAnimation(progressBar: ProgressBar) {
    progressBar.indeterminateDrawable = ColorDrawable(Color.BLUE)
}

fun matchesBackgroundColor(expectedResourceId: Int): Matcher<View?> {
    return object :
        BoundedMatcher<View?, View>(MaterialButton::class.java) {
        var actualColor = 0
        var expectedColor = 0
        var message: String? = null
        override fun matchesSafely(item: View): Boolean {
            if (item.background == null) {
                message = item.id.toString() + " does not have a background"
                return false
            }
            val resources: Resources = item.context.resources
            expectedColor = ResourcesCompat.getColor(resources, expectedResourceId, null)
            actualColor = try {
                (item.background as ColorDrawable).color
            } catch (e: Exception) {
                (item.background as GradientDrawable).color!!.defaultColor
            } finally {
                if (actualColor == expectedColor) {
                    Timber.i(
                        "Success...: Expected Color " + String.format(
                            "#%06X",
                            0xFFFFFF and expectedColor
                        ) + " Actual Color " + String.format(
                            "#%06X",
                            0xFFFFFF and actualColor
                        )
                    )
                }
            }
            return actualColor == expectedColor
        }

        override fun describeTo(description: Description) {
            if (actualColor != 0) {
                message = ("Background color did not match: Expected "
                        + String.format(
                    "#%06X",
                    0xFFFFFF and expectedColor
                ) + " was " + String.format("#%06X", 0xFFFFFF and actualColor))
            }
            description.appendText(message)
        }
    }
}

fun selectTabAtPosition(tabIndex: Int): ViewAction {
    return object : ViewAction {
        override fun getDescription() = "with tab at index $tabIndex"

        override fun getConstraints() =
            CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.isAssignableFrom(TabLayout::class.java)
            )

        override fun perform(uiController: UiController, view: View) {
            val tabLayout = view as TabLayout
            val tabAtIndex: TabLayout.Tab = tabLayout.getTabAt(tabIndex)
                ?: throw PerformException.Builder()
                    .withCause(Throwable("No tab at index $tabIndex"))
                    .build()

            tabAtIndex.select()
        }
    }
}



fun typeSearchViewText(query: String, submit: Boolean): ViewAction {
    return object : ViewAction {
        override fun getDescription(): String {
            return "Change view text"
        }

        override fun getConstraints(): Matcher<View> {
            return CoreMatchers.allOf(
                ViewMatchers.isDisplayed(),
                ViewMatchers.isAssignableFrom(SearchView::class.java)
            )
        }

        override fun perform(uiController: UiController?, view: View?) {
            (view as SearchView).setQuery(query, submit)
        }
    }
}
