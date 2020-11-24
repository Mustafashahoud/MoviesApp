package com.mustafa.movieguideapp.extension

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.mustafa.movieguideapp.utils.Constants.Companion.WALLPAPER_VIEW_TYPE

@Suppress("unused")
fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

@Suppress("unused")
fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

@Suppress("unused")
fun Fragment.showKeyboard() {
    view?.let { activity?.showKeyboard() }
}

@Suppress("unused")
fun Activity.showKeyboard() {
    showKeyboard()
}

@Suppress("unused")
fun Context.showKeyboard() {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
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
            return if (viewType == WALLPAPER_VIEW_TYPE) 1 else spanSize
        }
    }
    return gridLayoutManager
}
