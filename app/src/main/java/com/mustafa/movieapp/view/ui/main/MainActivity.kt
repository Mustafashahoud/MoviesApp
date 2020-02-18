
package com.mustafa.movieapp.view.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mustafa.movieapp.R
import com.mustafa.movieapp.view.ui.common.OnReselectedNavBottomViewItem
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity(), HasAndroidInjector {


  @Inject
  lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>
  var onReselectedNavBottomViewItem: OnReselectedNavBottomViewItem? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val navController = Navigation.findNavController(this, R.id.nav_host_container)

    bottom_navigation.setupWithNavController(navController)
    bottom_navigation.setOnNavigationItemReselectedListener {
      onReselectedNavBottomViewItem?.onReselectedNavBottomViewItem(it)
    }

  }

  private fun animateBottmNavView() {

  }






  override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

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