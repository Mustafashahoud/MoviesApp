package com.mustafa.movieguideapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mustafa.movieguideapp.api.ApiResponse
import com.mustafa.movieguideapp.api.MovieService
import com.mustafa.movieguideapp.mappers.KeywordPagingChecker
import com.mustafa.movieguideapp.mappers.ReviewPagingChecker
import com.mustafa.movieguideapp.mappers.VideoPagingChecker
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.models.network.KeywordListResponse
import com.mustafa.movieguideapp.models.network.ReviewListResponse
import com.mustafa.movieguideapp.models.network.VideoListResponse
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import com.mustafa.movieguideapp.view.ui.common.AppExecutors
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Original Source https://github.com/skydoves/TheMovies
 */
@OpenForTesting
@Singleton
class MovieRepository @Inject constructor(
        private val service: MovieService,
        private val movieDao: MovieDao,
        private val appExecutors: AppExecutors
)  {

    fun loadKeywordList(id: Int): LiveData<Resource<List<Keyword>>> {
        return object : NetworkBoundResource<
                List<Keyword>,
                KeywordListResponse,
                KeywordPagingChecker
                >(appExecutors) {
            override fun saveCallResult(items: KeywordListResponse) {
                val movie = movieDao.getMovie(id_ = id)
                movie.keywords = items.keywords
                movieDao.updateMovie(movie = movie)
            }

            override fun shouldFetch(data: List<Keyword>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Keyword>> {
                val movie = movieDao.getMovie(id_ = id)
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
                val movie = movieDao.getMovie(id_ = id)
                movie.videos = items.results
                movieDao.updateMovie(movie = movie)
            }

            override fun shouldFetch(data: List<Video>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Video>> {
                val movie = movieDao.getMovie(id_ = id)
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
                val movie = movieDao.getMovie(id_ = id)
                movie.reviews = items.results
                movieDao.updateMovie(movie = movie)
            }

            override fun shouldFetch(data: List<Review>?): Boolean {
                return data == null || data.isEmpty()
            }

            override fun loadFromDb(): LiveData<List<Review>> {
                val movie = movieDao.getMovie(id_ = id)
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
