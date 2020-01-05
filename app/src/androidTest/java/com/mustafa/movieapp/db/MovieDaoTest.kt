
package com.mustafa.movieapp.db

import androidx.test.runner.AndroidJUnit4
import com.mustafa.movieapp.models.entity.Movie
import com.mustafa.movieapp.utils.LiveDataTestUtil
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockMovie
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest : DbTest() {

  @Test
  fun insertAndReadTest() {
    val movieList = ArrayList<Movie>()
    val movie = mockMovie()
    movieList.add(movie)

    db.movieDao().insertMovieList(movieList)
    val loadFromDB = LiveDataTestUtil.getValue(db.movieDao().loadDiscoveryMovieList(movie.page))[0]
    assertThat(loadFromDB.page, `is`(1))
    assertThat(loadFromDB.id, `is`(123))
  }

  @Test
  fun updateAndReadTest() {
    val movieList = ArrayList<Movie>()
    val movie = mockMovie()
    movieList.add(movie)
    db.movieDao().insertMovieList(movieList)

    val loadFromDB = db.movieDao().getMovie(movie.id)
    assertThat(loadFromDB.page, `is`(1))

    movie.page = 10
    db.movieDao().updateMovie(movie)

    val updated = db.movieDao().getMovie(movie.id)
    assertThat(updated.page, `is`(10))
  }
}
