
package com.mustafa.movieguideapp.di

import com.mustafa.movieguideapp.view.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class MainActivityModule {

  @ContributesAndroidInjector (modules = [FragmentBuildersModule::class])
  abstract fun contributeMainActivity(): MainActivity
}
