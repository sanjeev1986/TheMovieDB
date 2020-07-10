package com.sample.themoviedb.utils.network

import com.sample.themoviedb.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor(
    private val domain: String,
    private val PARAM_API_KEY: String = "api_key",
    private val API_KEY: String = BuildConfig.apiKey
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.url().host() == domain) {
            val httpUrl = request.url().newBuilder().addQueryParameter(PARAM_API_KEY, API_KEY).build()
            request = request.newBuilder().url(httpUrl).build()
        }
        return chain.proceed(request)
    }
}
