package com.sample.themoviedb.di

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.sample.themoviedb.storage.StorageManager
import com.sample.themoviedb.storage.db.AppDatabase
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

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context) = AppDatabase.getDatabase(context)

    @Singleton
    @Provides
    fun provideWatchListEntity(appDatabase: AppDatabase) = appDatabase.watchListDao()

}