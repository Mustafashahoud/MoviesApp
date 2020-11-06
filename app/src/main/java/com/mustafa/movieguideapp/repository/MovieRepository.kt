package com.mustafa.movieguideapp.repository

import com.mustafa.movieguideapp.api.MovieService
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.room.MovieDao
import com.mustafa.movieguideapp.testing.OpenForTesting
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class MovieRepository @Inject constructor(
    private val service: MovieService,
    private val movieDao: MovieDao,
    private val dispatcherIO: CoroutineDispatcher
) {

    suspend fun loadKeywords(id: Int): Flow<Resource<List<Keyword>>> {
        return networkBoundResource(
            loadFromDb = {
                val movie = movieDao.getMovie(id_ = id)
                if (movie.keywords.isNullOrEmpty()) emptyList()
                else movie.keywords!!
            },
            fetchFromNetwork = { service.fetchKeywords(id = id) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val movie = movieDao.getMovie(id_ = id)
                movie.keywords = items.keywords
                movieDao.updateMovie(movie = movie)
            }
        )
    }

    suspend fun loadVideos(id: Int): Flow<Resource<List<Video>>> {
        return networkBoundResource(
            loadFromDb = {
                val movie = movieDao.getMovie(id_ = id)
                if (movie.videos.isNullOrEmpty()) emptyList()
                else movie.videos!!
            },
            fetchFromNetwork = { service.fetchVideos(id = id) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val movie = movieDao.getMovie(id_ = id)
                movie.videos = items.results
                movieDao.updateMovie(movie = movie)
            }
        )
    }

    suspend fun loadReviews(id: Int): Flow<Resource<List<Review>>> {
        return networkBoundResource(
            loadFromDb = {
                val movie = movieDao.getMovie(id_ = id)
                if (movie.reviews.isNullOrEmpty()) emptyList()
                else movie.reviews!!
            },
            fetchFromNetwork = { service.fetchReviews(id = id) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val movie = movieDao.getMovie(id_ = id)
                movie.reviews = items.results
                movieDao.updateMovie(movie = movie)
            }
        )
    }
}
