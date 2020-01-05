
package com.mustafa.movieapp.api.api

import com.mustafa.movieapp.api.ApiResponse
import org.hamcrest.CoreMatchers
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
    val apiResponse = ApiResponse<String>(exception)
    assertThat(apiResponse.isSuccessful, `is`(false))
    assertThat<String>(apiResponse.body, CoreMatchers.nullValue())
    assertThat(apiResponse.code, `is`(500))
    assertThat(apiResponse.message, `is`("foo"))
  }

  @Test
  fun success() {
    val apiResponse = ApiResponse(Response.success("foo"))
    assertThat(apiResponse.isSuccessful, `is`(true))
    assertThat(apiResponse.code, `is`(200))
    assertThat<String>(apiResponse.body, `is`("foo"))
    assertThat(apiResponse.message, CoreMatchers.nullValue())
  }
}
