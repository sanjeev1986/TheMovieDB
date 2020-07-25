package com.sample.themoviedb.di

import com.sample.themoviedb.api.ApiManager
import com.sample.themoviedb.utils.network.HttpStack
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApiModule {

    @Singleton
    @Provides
    fun provideApiManager(httpStack: HttpStack) = ApiManager(httpStack)

    @Provides
    fun provideMovieApi(apiManager: ApiManager) = apiManager.movieApi

    @Provides
    fun provideSearchApi(apiManager: ApiManager) = apiManager.searchApi

    @Provides
    fun provideGenreApi(apiManager: ApiManager) = apiManager.genreApi

    @Provides
    fun provideDiscoverApi(apiManager: ApiManager) = apiManager.discoverApi

    @Provides
    fun provideTrendingApi(apiManager: ApiManager) = apiManager.trendingApi
}
