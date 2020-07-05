package com.sample.themoviedb.storage

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.res.Configuration
import com.google.gson.Gson
import com.sample.themoviedb.storage.disk.DiskCache
import com.sample.themoviedb.storage.memory.InMemoryCache

class StorageManager(private val application: Application, private val gson: Gson) :
    ComponentCallbacks2 {

    val diskCache: DiskCache by lazy(LazyThreadSafetyMode.NONE) { DiskCache(application, gson) }
    val memoryCache by lazy(LazyThreadSafetyMode.NONE) { InMemoryCache() }

    override fun onLowMemory() {}

    override fun onConfigurationChanged(p0: Configuration?) {}

    override fun onTrimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            memoryCache.clearAll()
        }
    }
}
