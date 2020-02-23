
package com.mustafa.movieapp.view.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.extension.getCurrentNavigationFragment
import com.mustafa.movieapp.extension.isRecyclerViewScrollPositionZero
import com.mustafa.movieapp.extension.setSmoothScrollToZero
import com.mustafa.movieapp.view.ui.common.OnBackPressedMovieListFragment
import com.mustafa.movieapp.view.ui.common.OnBackPressedTvListFragment
import com.mustafa.movieapp.view.ui.common.OnReselectedNavBottomViewItem
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject


/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity(), HasAndroidInjector {


  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navController = Navigation.findNavController(this, R.id.nav_host_container)

    bottom_navigation.setupWithNavController(navController)
    bottom_navigation.setOnNavigationItemReselectedListener {
      when(it.itemId) {
        R.id.moviesFragment -> supportFragmentManager.getCurrentNavigationFragment()?.setSmoothScrollToZero(R.id.recyclerView_list_movies)
        R.id.tvsFragment -> supportFragmentManager.getCurrentNavigationFragment()?.setSmoothScrollToZero(R.id.recyclerView_list_tvs)
      }
    }
  }

  override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

  override fun onBackPressed() {


//    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_container)
//    val backStackEntryCount = navHostFragment?.childFragmentManager?.backStackEntryCount
//    for (x in 0 until backStackEntryCount!!) {
//      val entry = navHostFragment.childFragmentManager.getBackStackEntryAt(x)
//      Timber.d("BackStack ${entry.name}")
//    }

    val currentFragment: Fragment? = supportFragmentManager.getCurrentNavigationFragment()
    val currentFragmentName = (currentFragment as Fragment).javaClass.simpleName

    if (currentFragmentName == MOVIE_LIST_FRAGMENT) {
      if (!currentFragment.isRecyclerViewScrollPositionZero(R.id.recyclerView_list_movies)!!) {
        currentFragment.setSmoothScrollToZero(R.id.recyclerView_list_movies)
      } else {
        super.onBackPressed()
      }
    }

    else if (currentFragmentName == TV_LIST_FRAGMENT) {
      if (!currentFragment.isRecyclerViewScrollPositionZero(R.id.recyclerView_list_tvs)!!) {
        currentFragment.setSmoothScrollToZero(R.id.recyclerView_list_tvs)
      } else {
//        currentFragment.findNavController().popBackStack()
        super.onBackPressed()
      }
    }
    else super.onBackPressed()
  }
  companion object {
    const val MOVIE_LIST_FRAGMENT = "MovieListFragment"
    const val TV_LIST_FRAGMENT = "TvListFragment"
    const val CELEBRITY_LIST_FRAGMENT = "StarListFragment"
  }


}
//    if (savedInstanceState == null) {
//      setupBottomNavigationBar()
//    } // Else, need to wait for onRestoreInstanceState
//  }
//
//  override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
//    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
//    super.onRestoreInstanceState(savedInstanceState)
//    // Now that BottomNavigationBar has restored its instance state
//    // and its selectedItemId, we can proceed with setting up the
//    // BottomNavigationBar with Navigation
//    setupBottomNavigationBar()
//  }
//
//  /**
//   * Called on first creation and when restoring state.
//   */
//  private fun setupBottomNavigationBar() {
//    val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//
//    val navGraphIds = listOf(R.navigation.movies, R.navigation.tvs, R.navigation.stars)
//
//    // Setup the bottom navigation view with a list of navigation graphs
//    val controller = bottomNavigationView.setupWithNavController(
//            navGraphIds = navGraphIds,
//            fragmentManager = supportFragmentManager,
//            containerId = R.id.nav_host_container,
//            intent = intent
//    )
//
//
//
//     //Whenever the selected controller changes, setup the action bar.
////    controller.observe(this, Observer { navController ->
////      setupActionBarWithNavController(navController)
////    })
//    currentNavController = controller
//  }
//
//  override fun onSupportNavigateUp(): Boolean {
//    return currentNavController?.value?.navigateUp() ?: false
//  }