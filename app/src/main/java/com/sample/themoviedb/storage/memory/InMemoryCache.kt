@file:Suppress("UNCHECKED_CAST")

package com.sample.themoviedb.storage.memory

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.util.LruCache
import com.sample.themoviedb.storage.CacheResult
import java.lang.Exception
import kotlin.reflect.KClass

class InMemoryCache(size: Int = 1 * 1024 * 1024/* Default 1 MB cache*/) : ComponentCallbacks2 {
    override fun onLowMemory() {}

    override fun onConfigurationChanged(newConfig: Configuration?) {}

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            clearAll()
        }
    }

    private val memoryCache = LruCache<KClass<*>, Any?>(size)


    fun <T : Any> get(key: KClass<T>): CacheResult<T, Throwable> {
        val data = memoryCache[key]
        return try {
            data?.let { CacheResult.CacheHit(it as T) } ?: CacheResult.CacheMiss
        } catch (e: Exception) {
            CacheResult.CacheError(InvalidConversionOperation(e))
        }
    }

    fun <T : Any> put(key: KClass<T>, data: T): T {
        memoryCache.put(key, data)
        return data
    }

    fun <T : Any> remove(key: KClass<T>): CacheResult<T, Throwable> {
        val data = memoryCache.remove(key)
        return try {
            data?.let { CacheResult.CacheHit(it as T) } ?: CacheResult.CacheMiss
        } catch (e: Exception) {
            CacheResult.CacheError(e)
        }
    }

    fun clearAll() = memoryCache.evictAll()

    class InvalidConversionOperation(throwable: Throwable) : Throwable(throwable)
}

