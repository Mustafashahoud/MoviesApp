
package com.mustafa.movieguideapp.models

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
</T> */
data class Resource<out T> (
        val status: Status,
        val data: T?,
        val message: String?,
        val hasNextPage: Boolean
) {
  companion object {
    fun <T> success(data: T?, hasNextPage: Boolean): Resource<T> {
      return Resource(Status.SUCCESS, data, null, hasNextPage)
    }

    fun <T> error(msg: String, data: T?): Resource<T> {
      return Resource(Status.ERROR, data, msg, false)
    }

    fun <T> loading(data: T?): Resource<T> {
      return Resource(Status.LOADING, data, null, true)
    }
  }
}
