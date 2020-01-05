
package com.mustafa.movieapp.api.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mustafa.movieapp.api.ApiResponse
import retrofit2.Response

object ApiUtil {
  fun <T : Any> successCall(data: T) = createCall(Response.success(data))

  private fun <T : Any> createCall(response: Response<T>) = MutableLiveData<ApiResponse<T>>().apply {
    value = ApiResponse(response)
  } as LiveData<ApiResponse<T>>
}
