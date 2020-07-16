package com.sample.themoviedb.di

import android.app.Application
import com.google.gson.Gson
import com.sample.themoviedb.storage.StorageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideStorageManager(application: Application, gson: Gson) =
        StorageManager(application, gson)

    @Singleton
    @Provides
    fun provideDiskCache(storageManager: StorageManager) = storageManager.diskCache

    @Singleton
    @Provides
    fun provideInMemoryCache(storageManager: StorageManager) = storageManager.memoryCache


}