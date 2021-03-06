@file:Suppress("UNCHECKED_CAST")

package com.sample.themoviedb.storage.memory

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.util.LruCache

class InMemoryCache(size: Int = 1 * 1024 * 1024/* Default 1 MB cache*/) : ComponentCallbacks2 {
    override fun onLowMemory() {
        clearAll()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {}

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            clearAll()
        }
    }

    private val memoryCache = LruCache<String, Any?>(size)

    operator fun <T : Any> get(key: String): T? {
        val data = memoryCache[key]
        return try {
            data as T?
        } catch (e: Exception) {
            return null
        }
    }

    fun <T : Any> put(key: String, data: T): T {
        memoryCache.put(key, data)
        return data
    }

    fun <T : Any> remove(key: String): T? {
        val data = memoryCache.remove(key)
        return try {
            data as T?
        } catch (e: Exception) {
            return null
        }
    }

    fun clearAll() = memoryCache.evictAll()
}
