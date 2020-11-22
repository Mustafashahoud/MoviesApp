package com.mustafa.movieguideapp.repository

import com.mustafa.movieguideapp.api.*
import com.mustafa.movieguideapp.models.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn


inline fun <ResultType, RequestType> networkBoundResource(
    crossinline loadFromDb: suspend () -> ResultType,
    crossinline fetchFromNetwork: suspend () -> ApiResponse<RequestType>,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline pagingChecker: (RequestType) -> Boolean = { true },
    dispatcherIO: CoroutineDispatcher
) = flow<Resource<ResultType>> {
    emit(Resource.Loading())
    val data = loadFromDb()

    if (shouldFetch(data)) {
        emit(Resource.Loading())
        fetchFromNetwork().apply {
            this.onSuccessSuspend {
                this.data?.let {
                    saveFetchResult(it)
                    emit(Resource.Success(loadFromDb(), pagingChecker(it)))
                }

            }.onErrorSuspend {
                emit(Resource.Error(message()))
            }.onExceptionSuspend {
                emit(Resource.Error(message()))
            }
        }
    } else {
        Resource.Success(data, true)
    }
}.flowOn(dispatcherIO)

