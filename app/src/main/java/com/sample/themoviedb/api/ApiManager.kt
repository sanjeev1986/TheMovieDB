package com.sample.themoviedb.api

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.api.search.SearchApi
import com.sample.themoviedb.utils.network.HttpStack

class ApiManager(private val httpStack: HttpStack) : ComponentCallbacks2 {
    override fun onLowMemory() {

    }

    override fun onConfigurationChanged(p0: Configuration?) {
    }

    override fun onTrimMemory(level: Int) {

    }

    val movieApi by lazy(LazyThreadSafetyMode.NONE) { httpStack.retrofit.create(MovieApi::class.java) }

    val searchApi by lazy(LazyThreadSafetyMode.NONE) { httpStack.retrofit.create(SearchApi::class.java) }
}