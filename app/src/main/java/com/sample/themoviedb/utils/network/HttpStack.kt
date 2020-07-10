package com.sample.themoviedb.utils.network

import com.sample.themoviedb.BuildConfig
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Generic HTTP stack boiler plate. One instance per Domain
 */
class HttpStack(
    private val baseUrl: String,
    private val cacheDir: File
) {
    private val networkInterceptors: MutableList<Interceptor> =
        mutableListOf(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    private val appInterceptorList: MutableList<Interceptor> = mutableListOf(
        ApiKeyInterceptor(
            URL(BuildConfig.BASE_URL).host
        )
    )
    private val okhttpClient: OkHttpClient by lazy {
        val cacheSize = 10 * 1024 * 1024L // 10 MB
        val cache = Cache(cacheDir, cacheSize)
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECTION_TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(SOCKET_TIMEOUT_SECS, TimeUnit.SECONDS)
            .writeTimeout(SOCKET_TIMEOUT_SECS, TimeUnit.SECONDS)
            .cache(cache)
        networkInterceptors.forEach {
            builder.addInterceptor(it)
        }
        appInterceptorList.forEach {
            builder.addNetworkInterceptor(it)
        }
        builder.build()
    }

    val retrofit: Retrofit by lazy {
        val retrofitBuilder = Retrofit.Builder()
        retrofitBuilder
            .baseUrl(baseUrl)
            .client(okhttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
