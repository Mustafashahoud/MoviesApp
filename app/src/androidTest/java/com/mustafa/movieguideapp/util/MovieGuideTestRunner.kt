
package com.mustafa.movieguideapp.util

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.mustafa.movieguideapp.TestMovieApp

@Suppress("unused")
class MovieGuideTestRunner : AndroidJUnitRunner() {
  @Throws(InstantiationException::class, IllegalAccessException::class, ClassNotFoundException::class)
  override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
    return super.newApplication(cl, TestMovieApp::class.java.name, context)
  }
}
