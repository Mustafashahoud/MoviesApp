
package com.mustafa.movieguideapp

import android.app.Application

/**
 * We use a separate App for tests to prevent initializing dependency injection.
 *
 * See [com.mustafa.movieguideapp.util.MovieGuideTestRunner].
 */
class TestMovieApp : Application()
