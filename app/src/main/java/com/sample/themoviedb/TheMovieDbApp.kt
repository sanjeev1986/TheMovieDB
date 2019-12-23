package com.sample.themoviedb

import android.app.Application
import android.content.Context
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.sample.themoviedb.api.ApiManager
import com.sample.themoviedb.common.AppViewModerFactory
import com.sample.themoviedb.platform.PlatformManager
import com.sample.themoviedb.storage.StorageManager
import com.sample.themoviedb.utils.network.HttpStack
import timber.log.Timber

/**
 * GetAround app instances is the entry point to access api, storage & platform capabilities
 */
class TheMovieDbApp : Application() {
    companion object {
        fun getInstance(context: Context): TheMovieDbApp = context.applicationContext as TheMovieDbApp
    }

    override fun onCreate() {
        if (BuildConfig.DEBUG) {//
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectAll()
                    .build()
            )
            Timber.plant(Timber.DebugTree())
        }
        super.onCreate()

    }

    /**
     * Singleton HTTPStack instance
     */
    private val httpStack by lazy(LazyThreadSafetyMode.NONE) {
        HttpStack(
            BuildConfig.BASE_URL,
            cacheDir
        )
    }

    /**
     * Singleton storage provider
     */
    val storageManager by lazy(LazyThreadSafetyMode.NONE) { StorageManager(this) }

    /**
     * Singleton API Manager
     */
    val apiManager by lazy(LazyThreadSafetyMode.NONE) { ApiManager(httpStack) }

    /**
     * Singleton Viewmodel factory provider
     */
    val appViewModerFactory by lazy {
        AppViewModerFactory(this, apiManager, platformManager, storageManager)
    }

    /**
     * Singleton platform access
     */
    val platformManager = PlatformManager(this)


}