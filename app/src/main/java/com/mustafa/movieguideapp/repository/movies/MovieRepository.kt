package com.mustafa.movieguideapp.repository.movies

import com.mustafa.movieguideapp.api.*
import com.mustafa.movieguideapp.models.Keyword
import com.mustafa.movieguideapp.models.Resource
import com.mustafa.movieguideapp.models.Review
import com.mustafa.movieguideapp.models.Video
import com.mustafa.movieguideapp.testing.OpenForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton


@OpenForTesting
@Singleton
class MovieRepository @Inject constructor(private val service: MovieService) {

    suspend fun loadKeywords(id: Int): Flow<Resource<List<Keyword>>> {
        return flow {
            service.fetchKeywords(id = id).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Resource.Success(data.keywords, false))
                    }
                }.onErrorSuspend {
                    emit(Resource.Error(message()))
                }.onExceptionSuspend {
                    emit(Resource.Error(message()))
                }
            }
        }
    }

    suspend fun loadVideos(id: Int): Flow<Resource<List<Video>>> {
        return flow {
            service.fetchVideos(id = id).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Resource.Success(data.results, false))
                    }
                }.onErrorSuspend {
                    emit(Resource.Error(message()))
                }.onExceptionSuspend {
                    emit(Resource.Error(message()))
                }
            }
        }
    }

    suspend fun loadReviews(id: Int): Flow<Resource<List<Review>>> {
        return flow {
            service.fetchReviews(id = id).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Resource.Success(data.results, false))
                    }
                }.onErrorSuspend {
                    emit(Resource.Error(message()))
                }.onExceptionSuspend {
                    emit(Resource.Error(message()))
                }
            }
        }
    }
}
