package com.sample.themoviedb.di

import android.app.Application
import com.google.gson.Gson
import com.sample.themoviedb.BuildConfig
import com.sample.themoviedb.utils.network.HttpStack
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class HttpModule {

    @Singleton
    @Provides
    fun provideHttpStack(application: Application, gson: Gson) =
        HttpStack(BuildConfig.BASE_URL, application.cacheDir, gson)
}
