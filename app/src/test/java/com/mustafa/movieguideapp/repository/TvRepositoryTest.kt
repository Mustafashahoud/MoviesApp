package com.mustafa.movieguideapp.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mustafa.movieguideapp.api.TvService
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.network.KeywordListResponse
import com.mustafa.movieguideapp.models.network.ReviewListResponse
import com.mustafa.movieguideapp.models.network.VideoListResponse
import com.mustafa.movieguideapp.room.TvDao
import com.mustafa.movieguideapp.util.ApiUtil
import com.mustafa.movieguideapp.util.InstantAppExecutors
import com.mustafa.movieguideapp.utils.MockTestUtil
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockKeywordList
import com.mustafa.movieguideapp.utils.MockTestUtil.Companion.mockTv
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TvRepositoryTest {

    private lateinit var repository: TvRepository
    private val tvDao = mock<TvDao>()
    private val service = mock<TvService>()

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        repository = TvRepository(service, tvDao, InstantAppExecutors())
    }

    @Test
    fun loadKeywordListFromNetwork() {
        val loadFromDB = mockTv()
        whenever(tvDao.getTv(123)).thenReturn(loadFromDB)

        val mockResponse = KeywordListResponse(123, mockKeywordList())
        val call = ApiUtil.successCall(mockResponse)
        whenever(service.fetchKeywords(123)).thenReturn(call)

        val data = repository.loadKeywordList(123)
        verify(tvDao).getTv(123)
        verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<List<Keyword>>>>()
        data.observeForever(observer)
        verify(observer).onChanged(Resource.success(mockKeywordList(), false))

        val updatedTv = mockTv()
        updatedTv.keywords = mockKeywordList()
        verify(tvDao).updateTv(updatedTv)
    }

    @Test
    fun loadVideoListTest() {
        val loadFromDB = mockTv()
        whenever(tvDao.getTv(123)).thenReturn(loadFromDB)

        val mockResponse = VideoListResponse(123, MockTestUtil.mockVideoList())
        val call = ApiUtil.successCall(mockResponse)
        whenever(service.fetchVideos(123)).thenReturn(call)

        val data = repository.loadVideoList(123)
        verify(tvDao).getTv(123)
        verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<List<Video>>>>()
        data.observeForever(observer)
        verify(observer).onChanged(Resource.success(MockTestUtil.mockVideoList(), false))

        val updatedTv = mockTv()
        updatedTv.videos = MockTestUtil.mockVideoList()
        verify(tvDao).updateTv(updatedTv)
    }

    @Test
    fun loadReviewListTest() {
        val loadFromDB = mockTv()
        whenever(tvDao.getTv(123)).thenReturn(loadFromDB)

        val mockResponse = ReviewListResponse(123, 1, MockTestUtil.mockReviewList(), 100, 100)
        val call = ApiUtil.successCall(mockResponse)
        whenever(service.fetchReviews(123)).thenReturn(call)

        val data = repository.loadReviewsList(123)
        verify(tvDao).getTv(123)
        verifyNoMoreInteractions(service)

        val observer = mock<Observer<Resource<List<Review>>>>()
        data.observeForever(observer)
        verify(observer).onChanged(Resource.success(MockTestUtil.mockReviewList(), false))

        val updatedTv = mockTv()
        updatedTv.reviews = MockTestUtil.mockReviewList()
        verify(tvDao).updateTv(updatedTv)
    }
}
