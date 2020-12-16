@file:Suppress("unused")

package com.mustafa.movieguideapp.extension

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.utils.Constants
import kotlin.math.max


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * This method returns a GridLayoutManager with spanSize 1 when the PagingDataAdapter is loading to show the load_state_item in the center
 * as if it was linearLayoutManager and with spanSize [spanSize] when it is not loading.
 * set span size to 1 in GridLayoutManager when LoadState is Loading in Paging Library
 */
fun Fragment.getGridLayoutManagerWithSpanSizeOne(
    adapter: PagingDataAdapter<*, *>,
    spanSize: Int
): GridLayoutManager {
    val gridLayoutManager = GridLayoutManager(requireContext(), spanSize)
    gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            val viewType = adapter.getItemViewType(position)
            return if (viewType == Constants.WALLPAPER_VIEW_TYPE) 1 else spanSize
        }
    }
    return gridLayoutManager
}

@SuppressLint("ObsoleteSdkInt")
fun checkIsMaterialVersion() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun Fragment.circularRevealedAtCenter(view: View) {
    val cx = (view.left + view.right) / 2
    val cy = (view.top + view.bottom) / 2
    val finalRadius = max(view.width, view.height)

    if (checkIsMaterialVersion() && view.isAttachedToWindow) {
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0f, finalRadius.toFloat())
        view.visible()
        view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.background))
        anim.duration = 550
        anim.start()
    }
}

fun Fragment.requestGlideListener(view: View): RequestListener<Drawable> {
    return object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            circularRevealedAtCenter(view)
            return false
        }
    }
}

fun Fragment.simpleToolbarWithHome(toolbar: Toolbar, title_: String = "") {
    if (activity is AppCompatActivity) {
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
    }
    (activity as AppCompatActivity).supportActionBar?.run {
        setDisplayHomeAsUpEnabled(true)
        setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
        title = title_
    }
}

fun Fragment.applyToolbarMargin(toolbar: Toolbar) {
    if (checkIsMaterialVersion()) {
        toolbar.layoutParams =
            (toolbar.layoutParams as CollapsingToolbarLayout.LayoutParams).apply {
                topMargin = getStatusBarSize()
            }
    }
}

private fun Fragment.getStatusBarSize(): Int {
    val idStatusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (idStatusBarHeight > 0) {
        resources.getDimensionPixelSize(idStatusBarHeight)
    } else 0
}


fun FragmentManager.getCurrentNavigationFragment(): Fragment? =
    primaryNavigationFragment?.childFragmentManager?.fragments?.first()

/**
 * true if the findFirstCompletelyVisibleItemPosition is Zero
 */
fun Fragment.isRecyclerViewScrollPositionZero(id: Int): Boolean =
    (activity?.findViewById<RecyclerView>(id)?.layoutManager as GridLayoutManager).findFirstCompletelyVisibleItemPosition() == 0

fun Fragment.setSmoothScrollToZero(resId: Int) {
    activity?.findViewById<RecyclerView>(resId)?.smoothScrollToPosition(0)

}

fun Fragment.isEmptyOrBlank(text: String) = text.isEmpty() || text.isBlank()
