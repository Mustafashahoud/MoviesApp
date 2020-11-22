package com.mustafa.movieguideapp.util

/**
 * Copied for https://github.com/android/architecture-components-samples/tree/master/GithubBrowserSample
 */
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.espresso.IdlingRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * A JUnit rule that registers an idling resource for all fragment views that use data binding.
 */
class DataBindingIdlingResourceRule : TestWatcher() {
    private val idlingResource = DataBindingIdlingResource()
    
    fun monitorFragment(fragmentScenario: FragmentScenario<out Fragment>) {
        idlingResource.monitorFragment(fragmentScenario)
    }

    override fun finished(description: Description?) {
        IdlingRegistry.getInstance().unregister(idlingResource)
        super.finished(description)
    }

    override fun starting(description: Description?) {
        IdlingRegistry.getInstance().register(idlingResource)
        super.starting(description)
    }

}