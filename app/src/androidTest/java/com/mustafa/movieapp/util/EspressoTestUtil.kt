package com.mustafa.movieapp.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
/**
 * Copied for https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample
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
