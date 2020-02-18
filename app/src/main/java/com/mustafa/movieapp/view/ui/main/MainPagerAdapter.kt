
package com.mustafa.movieapp.view.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mustafa.movieapp.view.ui.movies.movielist.MovieListFragment
import com.mustafa.movieapp.view.ui.person.CelebritiesListFragment
import com.mustafa.movieapp.view.ui.tv.tvlist.TvListFragment

class MainPagerAdapter(fm: FragmentManager)
  : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

  override fun getItem(position: Int): Fragment {
    return when (position) {
      0 -> MovieListFragment()
      1 -> TvListFragment()
      else -> CelebritiesListFragment()
    }
  }

  override fun getCount() = 3
}
