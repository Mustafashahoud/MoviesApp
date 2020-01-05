
package com.mustafa.movieapp.api.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.mustafa.movieapp.api.MovieService
import com.mustafa.movieapp.api.api.ApiUtil.successCall
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.network.KeywordListResponse
import com.mustafa.movieapp.models.network.ReviewListResponse
import com.mustafa.movieapp.models.network.VideoListResponse
import com.mustafa.movieapp.repository.MovieRepository
import com.mustafa.movieapp.room.MovieDao
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockKeywordList
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockMovie
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockReviewList
import com.mustafa.movieapp.utils.MockTestUtil.Companion.mockVideoList
import com.mustafa.movieapp.view.ui.movies.moviedetail.MovieDetailViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MovieDetailVIewModelTest {

  private lateinit var viewModel: MovieDetailViewModel

  private lateinit var repository: MovieRepository
  private val movieDao = mock<MovieDao>()

  private val service = mock<MovieService>()

  @Rule
  @JvmField
  val instantExecutorRule = InstantTaskExecutorRule()

  @Before
  fun init() {
    repository = MovieRepository(service, movieDao)
    viewModel =
        MovieDetailViewModel(
            repository
        )
  }

  @Test
  fun loadKeywordList() {
    val loadFromDB = mockMovie()
    whenever(movieDao.getMovie(123)).thenReturn(loadFromDB)

    val mockResponse = KeywordListResponse(123, mockKeywordList())
    val call = successCall(mockResponse)
    whenever(service.fetchKeywords(123)).thenReturn(call)

    val data = repository.loadKeywordList(123)
    val observer = mock<Observer<Resource<List<Keyword>>>>()
    data.observeForever(observer)

    viewModel.postMovieId(123)
    verify(movieDao, times(3)).getMovie(123)
    verify(observer).onChanged(
        Resource.success(mockKeywordList(), true))

    val updatedMovie = mockMovie()
    updatedMovie.keywords = mockKeywordList()
    verify(movieDao).updateMovie(updatedMovie)
  }

  @Test
  fun loadVideoList() {
    val loadFromDB = mockMovie()
    whenever(movieDao.getMovie(123)).thenReturn(loadFromDB)

    val mockResponse = VideoListResponse(123, mockVideoList())
    val call = successCall(mockResponse)
    whenever(service.fetchVideos(123)).thenReturn(call)

    val data = repository.loadVideoList(123)
    val observer = mock<Observer<Resource<List<Video>>>>()
    data.observeForever(observer)

    viewModel.postMovieId(123)
    verify(movieDao, times(3)).getMovie(123)
    verify(observer).onChanged(
        Resource.success(mockVideoList(), true)
    )

    val updatedMovie = mockMovie()
    updatedMovie.videos = mockVideoList()
    verify(movieDao).updateMovie(updatedMovie)
  }

  @Test
  fun loadReviewList() {
    val loadFromDB = mockMovie()
    whenever(movieDao.getMovie(123)).thenReturn(loadFromDB)

    val mockResponse = ReviewListResponse(123, 1, mockReviewList(), 100, 100)
    val call = successCall(mockResponse)
    whenever(service.fetchReviews(123)).thenReturn(call)

    val data = repository.loadReviewsList(123)
    val observer = mock<Observer<Resource<List<Review>>>>()
    data.observeForever(observer)

    viewModel.postMovieId(123)
    verify(movieDao, times(3)).getMovie(123)
    verify(observer).onChanged(
        Resource.success(mockReviewList(), true)
    )

    val updatedMovie = mockMovie()
    updatedMovie.reviews = mockReviewList()
    verify(movieDao).updateMovie(updatedMovie)
  }
}
