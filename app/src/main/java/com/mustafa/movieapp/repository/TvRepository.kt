package com.mustafa.movieapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mustafa.movieapp.api.ApiResponse
import com.mustafa.movieapp.api.TvService
import com.mustafa.movieapp.mappers.KeywordPagingChecker
import com.mustafa.movieapp.mappers.ReviewPagingChecker
import com.mustafa.movieapp.mappers.VideoPagingChecker
import com.mustafa.movieapp.models.Keyword
import com.mustafa.movieapp.models.Resource
import com.mustafa.movieapp.models.Review
import com.mustafa.movieapp.models.Video
import com.mustafa.movieapp.models.network.KeywordListResponse
import com.mustafa.movieapp.models.network.ReviewListResponse
import com.mustafa.movieapp.models.network.VideoListResponse
import com.mustafa.movieapp.room.TvDao
import com.mustafa.movieapp.view.ui.common.AppExecutors
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvRepository @Inject constructor(
        private val service: TvService,
        private val tvDao: TvDao,
        private val appExecutors: AppExecutors)
    : Repository {


    fun loadKeywordList(id: Int): LiveData<Resource<List<Keyword>>> {
        return object : NetworkBoundResource<
                List<Keyword>,
                KeywordListResponse,
                KeywordPagingChecker
                >(appExecutors) {
            override fun saveCallResult(items: KeywordListResponse) {
                val tv = tvDao.getTv(id_ = id)
                tv.keywords = items.keywords
                tvDao.updateTv(tv = tv)
            }

            override fun shouldFetch(data: List<Keyword>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Keyword>> {
                val movie = tvDao.getTv(id_ = id)
                val data: MutableLiveData<List<Keyword>> = MutableLiveData()
                data.value = movie.keywords
                return data
            }

            override fun pageChecker(): KeywordPagingChecker {
                return KeywordPagingChecker()
            }
            override fun createCall(): LiveData<ApiResponse<KeywordListResponse>> {
                return service.fetchKeywords(id = id)
            }
        }.asLiveData()
    }

    fun loadVideoList(id: Int): LiveData<Resource<List<Video>>> {
        return object : NetworkBoundResource<
                List<Video>,
                VideoListResponse,
                VideoPagingChecker
                >(appExecutors) {
            override fun saveCallResult(items: VideoListResponse) {
                val tv = tvDao.getTv(id_ = id)
                tv.videos = items.results
                tvDao.updateTv(tv = tv)
            }

            override fun shouldFetch(data: List<Video>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Video>> {
                val movie = tvDao.getTv(id_ = id)
                val data: MutableLiveData<List<Video>> = MutableLiveData()
                data.value = movie.videos
                return data
            }

            override fun pageChecker(): VideoPagingChecker {
                return VideoPagingChecker()
            }
            override fun createCall(): LiveData<ApiResponse<VideoListResponse>> {
                return service.fetchVideos(id = id)
            }
        }.asLiveData()
    }

    fun loadReviewsList(id: Int): LiveData<Resource<List<Review>>> {
        return object : NetworkBoundResource<
                List<Review>,
                ReviewListResponse,
                ReviewPagingChecker
                >(appExecutors) {
            override fun saveCallResult(items: ReviewListResponse) {
                val tv = tvDao.getTv(id_ = id)
                tv.reviews = items.results
                tvDao.updateTv(tv = tv)
            }

            override fun shouldFetch(data: List<Review>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Review>> {
                val movie = tvDao.getTv(id_ = id)
                val data: MutableLiveData<List<Review>> = MutableLiveData()
                data.value = movie.reviews
                return data
            }

            override fun pageChecker(): ReviewPagingChecker {
                return ReviewPagingChecker()
            }
            override fun createCall(): LiveData<ApiResponse<ReviewListResponse>> {
                return service.fetchReviews(id = id)
            }
        }.asLiveData()
    }
}
