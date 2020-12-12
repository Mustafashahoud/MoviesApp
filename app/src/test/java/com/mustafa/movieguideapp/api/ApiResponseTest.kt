package com.mustafa.movieguideapp.api

import okhttp3.ResponseBody.Companion.toResponseBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Response

@RunWith(JUnit4::class)
class ApiResponseTest {

    @Test
    fun exception() {
        val exception = Exception("foo")
        val apiErrorResponse = ApiResponse.create<String>(exception)
        assertThat(apiErrorResponse.errorMessage, `is`("foo"))
    }

    @Test
    fun success() {
        val apiResponse = ApiResponse.create<String>(
            Response.success("foo")
        ) as ApiSuccessResponse<String>
        assertThat(apiResponse.body, `is`("foo"))
    }

    @Test
    fun error() {
        val errorResponse = Response.error<String>(400, "Mustafa".toResponseBody())
        val errorApi = ApiResponse.create<String>(errorResponse) as ApiErrorResponse
        assertThat(errorApi.errorMessage, `is`("Mustafa"))

    }
}
