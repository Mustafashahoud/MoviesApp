package com.mustafa.movieguideapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.repository.MovieRepository
import com.mustafa.movieguideapp.view.ui.movies.moviedetail.MovieDetailViewModel
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class MovieDetailViewModelTest {

    @Rule
    @JvmField
    val instantExecutor = InstantTaskExecutorRule()

    private lateinit var viewModel: MovieDetailViewModel
    private val repository = mock<MovieRepository>()

    @Before
    fun init() {
        viewModel = MovieDetailViewModel(repository = repository)
    }

    @Test
    fun testWithNullPageNum() {
        val observerKeyword = mock<Observer<Resource<List<Keyword>>>>()
        val observerReview = mock<Observer<Resource<List<Review>>>>()
        val observerVideo = mock<Observer<Resource<List<Video>>>>()
        viewModel.keywordListLiveData.observeForever(observerKeyword)
        viewModel.reviewListLiveData.observeForever(observerReview)
        viewModel.videoListLiveData.observeForever(observerVideo)

        viewModel.setMovieId(null)
        verifyNoMoreInteractions(repository)
        verify(repository, never()).loadKeywords(Mockito.anyInt())
        verify(repository, never()).loadReviews(Mockito.anyInt())
        verify(repository, never()).loadVideos(Mockito.anyInt())
    }

    @Test
    fun testWithPageNumberNotNull() {
        val observerKeyword = mock<Observer<Resource<List<Keyword>>>>()
        val observerReview = mock<Observer<Resource<List<Review>>>>()
        val observerVideo = mock<Observer<Resource<List<Video>>>>()
        viewModel.keywordListLiveData.observeForever(observerKeyword)
        viewModel.reviewListLiveData.observeForever(observerReview)
        viewModel.videoListLiveData.observeForever(observerVideo)

        viewModel.setMovieId(1)
        verify(repository).loadKeywords(1)
        verify(repository).loadReviews(1)
        verify(repository).loadVideos(1)
    }

}