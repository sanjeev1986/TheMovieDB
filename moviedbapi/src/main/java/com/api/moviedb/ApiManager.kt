package com.api.moviedb

import android.content.ComponentCallbacks2
import android.content.Context
import android.content.res.Configuration
import com.api.moviedb.movies.MovieApi
import com.api.moviedb.search.SearchApi
import com.api.moviedb.utils.network.HttpStack

class ApiManager(private val context: Context) : ComponentCallbacks2 {
    /**
     * Singleton HTTPStack instance
     */
    private val httpStack by lazy(LazyThreadSafetyMode.NONE) {
        HttpStack(
            BuildConfig.BASE_URL,
            context.cacheDir
        )
    }
    override fun onLowMemory() {

    }

    override fun onConfigurationChanged(p0: Configuration?) {
    }

    override fun onTrimMemory(level: Int) {

    }

    val movieApi by lazy(LazyThreadSafetyMode.NONE) { MovieApi(httpStack) }
    val searchApi by lazy(LazyThreadSafetyMode.NONE) { SearchApi(httpStack) }
}