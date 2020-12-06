package com.mustafa.movieguideapp.view.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mustafa.movieguideapp.R
import com.mustafa.movieguideapp.databinding.ActivityMainBinding
import com.mustafa.movieguideapp.extension.*
import com.mustafa.movieguideapp.utils.setupWithNavController
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity(), HasAndroidInjector {
    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    lateinit var binding: ActivityMainBinding

    private var currentNavController: LiveData<NavController>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

//        setSplashy()
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
        setOnNavigationItemReselected()

        currentNavController?.observe(this) { navController ->
            navController.addOnDestinationChangedListener { _, destination, _ ->
                if (isMainFragment(destination)) {
                    binding.bottomNavigation.visible()
                } else {
                    binding.bottomNavigation.gone()
                }
            }
        }
    }

    override fun onBackPressed() {
//    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)
//    val backStackEntryCount = navHostFragment?.childFragmentManager?.backStackEntryCount
//    for (x in 0 until backStackEntryCount!!) {
//      val entry = navHostFragment.childFragmentManager.getBackStackEntryAt(x)
//      Timber.d("BackStack ${entry.name}")
//    }
        val currentFragment = supportFragmentManager.getCurrentNavigationFragment()

        when (currentFragment?.id) {
            R.id.moviesFragment -> {
                if (!currentFragment.isRecyclerViewScrollPositionZero(R.id.recyclerView_list_movies)) {
                    currentFragment.setSmoothScrollToZero(R.id.recyclerView_list_movies)
                } else {
                    super.onBackPressed()
                }
            }

            R.id.tvsFragment -> {
                if (!currentFragment.isRecyclerViewScrollPositionZero(R.id.recyclerView_list_tvs)) {
                    currentFragment.setSmoothScrollToZero(R.id.recyclerView_list_tvs)
                } else {
                    super.onBackPressed()
                }
            }

            R.id.celebritiesFragment -> {
                if (!currentFragment.isRecyclerViewScrollPositionZero(R.id.recyclerView_list_celebrities)) {
                    currentFragment.setSmoothScrollToZero(R.id.recyclerView_list_celebrities)
                } else {
                    super.onBackPressed()
                }
            }

            else -> super.onBackPressed()

        }
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val navGraphIds = listOf(R.navigation.movie, R.navigation.tv, R.navigation.star)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        currentNavController = controller
    }


    private fun setOnNavigationItemReselected() {
        binding.bottomNavigation.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.movie -> supportFragmentManager.getCurrentNavigationFragment()
                    ?.setSmoothScrollToZero(R.id.recyclerView_list_movies)
                R.id.tv -> supportFragmentManager.getCurrentNavigationFragment()
                    ?.setSmoothScrollToZero(R.id.recyclerView_list_tvs)
                R.id.star -> supportFragmentManager.getCurrentNavigationFragment()
                    ?.setSmoothScrollToZero(R.id.recyclerView_list_celebrities)
            }
        }
    }


    private fun isMainFragment(destination: NavDestination): Boolean =
        destination.id == R.id.moviesFragment
                || destination.id == R.id.tvsFragment
                || destination.id == R.id.celebritiesFragment


    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector
}




