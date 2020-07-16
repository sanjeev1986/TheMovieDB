package com.sample.themoviedb

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.sample.themoviedb.api.ApiManager
import com.sample.themoviedb.di.ApplicationComponent
import com.sample.themoviedb.di.DaggerApplicationComponent
import com.sample.themoviedb.platform.PlatformManager
import com.sample.themoviedb.repositories.RepositoryManager
import com.sample.themoviedb.storage.StorageManager
import com.sample.themoviedb.utils.network.HttpStack
import timber.log.Timber

/**
 * GetAround app instances is the entry point to access api, storage & platform capabilities
 */
open class TheMovieDbApp : Application() {
    companion object {
        fun getInstance(context: Context): TheMovieDbApp =
            context.applicationContext as TheMovieDbApp

        fun getInstance(activity: AppCompatActivity): TheMovieDbApp =
            activity.applicationContext as TheMovieDbApp

        fun getInstance(fragment: Fragment): TheMovieDbApp =
            fragment.requireActivity().applicationContext as TheMovieDbApp
    }

    val applicationComponent: ApplicationComponent by lazy {
        DaggerApplicationComponent.builder().application(this).context(this).build()
    }

    override fun onCreate() {
        if (BuildConfig.DEBUG) { //
            /* StrictMode.setThreadPolicy(
                 StrictMode.ThreadPolicy.Builder()
                     .detectAll()
                     .penaltyLog()
                     .build()
             )
             StrictMode.setVmPolicy(
                 VmPolicy.Builder()
                     .detectAll()
                     .build()
             )*/
            Timber.plant(Timber.DebugTree())
        }
        super.onCreate()
    }

    private val gson = Gson()

    /**
     * Singleton HTTPStack instance
     */
    private val httpStack by lazy(LazyThreadSafetyMode.NONE) {
        HttpStack(BuildConfig.BASE_URL, cacheDir, gson)
    }

    /**
     * Singleton storage provider
     */
    private val storageManager by lazy(LazyThreadSafetyMode.NONE) { StorageManager(this, gson) }

    /**
     * Singleton API Manager
     */
    private val apiManager by lazy(LazyThreadSafetyMode.NONE) { ApiManager(httpStack) }

    /**
     * Singleton Repository Manager
     */
    private val repositoryManager by lazy(LazyThreadSafetyMode.NONE) {
        RepositoryManager(
            apiManager,
            storageManager,
            platformManager
        )
    }

    /**
     * Singleton platform access
     */
    private val platformManager = PlatformManager(this)

}
