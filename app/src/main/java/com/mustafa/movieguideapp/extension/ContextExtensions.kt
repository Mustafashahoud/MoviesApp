package com.mustafa.movieguideapp.extension

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

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