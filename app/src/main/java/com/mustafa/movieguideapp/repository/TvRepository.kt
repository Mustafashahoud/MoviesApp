package com.mustafa.movieguideapp.repository

import com.mustafa.movieguideapp.api.TvService
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.room.TvDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TvRepository @Inject constructor(
    private val service: TvService,
    private val tvDao: TvDao,
    private val dispatcherIO: CoroutineDispatcher
) {

    suspend fun loadKeywords(id: Int): Flow<Resource<List<Keyword>>> {
        return networkBoundResource(
            loadFromDb = {
                val movie = tvDao.getTv(id_ = id)
                if (movie.keywords.isNullOrEmpty()) emptyList()
                else movie.keywords!!
            },
            fetchFromNetwork = { service.fetchKeywords(id = id) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val tv = tvDao.getTv(id_ = id)
                tv.keywords = items.keywords
                tvDao.updateTv(tv = tv)
            }
        )
    }

    suspend fun loadVideos(id: Int): Flow<Resource<List<Video>>> {
        return networkBoundResource(
            loadFromDb = {
                val movie = tvDao.getTv(id_ = id)
                if (movie.videos.isNullOrEmpty()) emptyList()
                else movie.videos!!
            },
            fetchFromNetwork = { service.fetchVideos(id = id) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val tv = tvDao.getTv(id_ = id)
                tv.videos = items.results
                tvDao.updateTv(tv = tv)
            }
        )
    }


    suspend fun loadReviews(id: Int): Flow<Resource<List<Review>>> {
        return networkBoundResource(
            loadFromDb = {
                val movie = tvDao.getTv(id_ = id)
                if (movie.reviews.isNullOrEmpty()) emptyList()
                else movie.reviews!!
            },
            fetchFromNetwork = { service.fetchReviews(id = id) },
            dispatcherIO = dispatcherIO,
            pagingChecker = { false },
            saveFetchResult = { items ->
                val tv = tvDao.getTv(id_ = id)
                tv.reviews = items.results
                tvDao.updateTv(tv = tv)
            }
        )
    }
}
