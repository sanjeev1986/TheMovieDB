package com.sample.themoviedb.api

import com.sample.themoviedb.api.discover.DiscoverApi
import com.sample.themoviedb.api.genres.GenreApi
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.api.search.SearchApi
import com.sample.themoviedb.api.trending.TrendingApi
import com.sample.themoviedb.utils.network.HttpStack

class ApiManager(private val httpStack: HttpStack) {

    val movieApi by lazy(LazyThreadSafetyMode.NONE) { httpStack.retrofit.create(MovieApi::class.java) }

    val searchApi by lazy(LazyThreadSafetyMode.NONE) { httpStack.retrofit.create(SearchApi::class.java) }

    val genreApi by lazy(LazyThreadSafetyMode.NONE) { httpStack.retrofit.create(GenreApi::class.java) }

    val discoverApi by lazy(LazyThreadSafetyMode.NONE) { httpStack.retrofit.create(DiscoverApi::class.java) }

    val trendingApi by lazy(LazyThreadSafetyMode.NONE) { httpStack.retrofit.create(TrendingApi::class.java) }
}
