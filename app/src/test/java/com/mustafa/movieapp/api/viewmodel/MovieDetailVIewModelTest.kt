package com.mustafa.movieapp.api.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.repository.MovieRepository
import com.mustafa.movieapp.view.ui.movies.moviedetail.MovieDetailViewModel
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
        verify(repository, never()).loadKeywordList(Mockito.anyInt())
        verify(repository, never()).loadReviewsList(Mockito.anyInt())
        verify(repository, never()).loadVideoList(Mockito.anyInt())
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
        verify(repository).loadKeywordList(1)
        verify(repository).loadReviewsList(1)
        verify(repository).loadVideoList(1)
    }

}