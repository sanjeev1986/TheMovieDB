@file:Suppress("UNCHECKED_CAST")

package com.sanj.appstarterpack.storage.memory

import android.content.ComponentCallbacks2
import android.content.res.Configuration
import android.util.LruCache
import io.reactivex.Maybe
//import kotlinx.coroutines.rx2.await
//import kotlinx.coroutines.rx2.openSubscription
import java.lang.Exception

class InMemoryCache(size: Int = 1 * 1024 * 1024/* Default 1 MB cache*/) : ComponentCallbacks2 {
    override fun onLowMemory() {}

    override fun onConfigurationChanged(newConfig: Configuration?) {}

    override fun onTrimMemory(level: Int) {
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW) {
            clearAll()
        }
    }

    private val memoryCache = LruCache<Class<*>, Any>(size)


    fun <T> get(key: Class<T>): Maybe<T> {
        val data = memoryCache[key]
        return if (data != null)
            try {
                val result: T = data as T
                Maybe.just(result)
            } catch (e: Exception) {
                Maybe.error<T>(InvalidConversionOperation(e))
            }
        else
            Maybe.empty()
    }


    fun <T> put(key: Class<T>, data: T): T {
        memoryCache.put(key, data)
        return data
    }

    fun <T> remove(key: Class<T>) = memoryCache.remove(key) as T?

    fun clearAll() = memoryCache.evictAll()

    class InvalidConversionOperation(throwable: Throwable) : Throwable(throwable)
}