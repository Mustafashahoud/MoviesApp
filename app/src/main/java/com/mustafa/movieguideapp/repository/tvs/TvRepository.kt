package com.mustafa.movieguideapp.repository.tvs

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
class TvRepository @Inject constructor(private val service: TvService) {

    suspend fun loadKeywords(id: Int): Flow<Resource<List<Keyword>>> {
        return flow {
            service.fetchKeywords(id = id).apply {
                this.onSuccessSuspend {
                    data?.let {
                        emit(Resource.Success(it.keywords, false))
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
                onSuccessSuspend {
                    data?.let {
                        emit(Resource.Success(it.results, false))
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
                onSuccessSuspend {
                    data?.let {
                        emit(Resource.Success(it.results, false))
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
