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
    emit(Resource.loading(null))
    val data = loadFromDb()

    if (shouldFetch(data)) {
        emit(Resource.loading(data))
        fetchFromNetwork().apply {
            this.onSuccessSuspend {
                this.data?.let {
                    saveFetchResult(it)
                    emit(Resource.success(loadFromDb(), pagingChecker(it)))
                }

            }.onErrorSuspend {
                emit(Resource.error(message(), null))
            }.onExceptionSuspend {
                emit(Resource.error(message(), null))
            }
        }
    } else {
        Resource.success(data, true)
    }
}.flowOn(dispatcherIO)

