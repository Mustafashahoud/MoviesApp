package com.mustafa.movieguideapp.api

import com.mustafa.movieguideapp.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

/**
 *  Copied from https://github.com/skydoves/TheMovies
 */
internal class RequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url
        val url = originalUrl.newBuilder()
            .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
            .build()

        val requestBuilder = originalRequest.newBuilder().url(url)
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
