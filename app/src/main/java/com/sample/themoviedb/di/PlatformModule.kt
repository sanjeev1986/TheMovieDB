package com.sample.themoviedb.di

import android.app.Application
import com.sample.themoviedb.platform.PlatformManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PlatformModule {

    @Singleton
    @Provides
    fun providePlatformManager(application: Application) =
        PlatformManager(application)

    @Singleton
    @Provides
    fun provideNetworkManager(platformManager: PlatformManager) = platformManager.networkManager
}
