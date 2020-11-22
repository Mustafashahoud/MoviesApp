package com.mustafa.movieguideapp.models

// A generic class that contains data and status about loading this data.
sealed class Resource<out T>(
    val data: T? = null,
    val message: String? = null,
    val hasNextPage: Boolean
) {
    class Success<T>(data: T, hasNextPage: Boolean) : Resource<T>(data, hasNextPage = hasNextPage)
    class Loading : Resource<Nothing>(null, hasNextPage = false)
    class Error(message: String) : Resource<Nothing>(null, message = message, hasNextPage = false)
}
