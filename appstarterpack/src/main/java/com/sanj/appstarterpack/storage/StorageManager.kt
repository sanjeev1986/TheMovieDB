package com.sanj.appstarterpack.storage

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.sanj.appstarterpack.storage.disk.SharedPrefCache
import com.sanj.appstarterpack.storage.memory.InMemoryCache

class StorageManager(private val application: Application) : ComponentCallbacks2 {

    val memoryCache by lazy(LazyThreadSafetyMode.NONE) { InMemoryCache() }
    val sharedPref by lazy(LazyThreadSafetyMode.NONE) {
        SharedPrefCache(application)
    }

    override fun onLowMemory() {}

    override fun onConfigurationChanged(p0: Configuration?) {}

    override fun onTrimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            memoryCache.clearAll()
        }
    }
}