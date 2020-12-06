package com.mustafa.movieguideapp.view.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.mustafa.movieguideapp.extension.visible
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

abstract class AutoDisposeFragment(intRes: Int) : Fragment(intRes) {

    protected val scopeProvider: AndroidLifecycleScopeProvider by lazy {
        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
    }

    protected fun showBottomNavigationView() {
        (activity as MainActivity).binding.bottomNavigation.visible()
    }
}