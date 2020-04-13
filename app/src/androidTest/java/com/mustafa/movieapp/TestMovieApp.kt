
package com.mustafa.movieapp

import android.app.Application

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See [com.mustafa.movieapp.util.MovieGuideTestRunner].
 */
class TestMovieApp : Application()
