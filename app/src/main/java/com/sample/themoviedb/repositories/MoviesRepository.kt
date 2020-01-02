package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.api.movies.NowInThreatresResponse
import com.sanj.appstarterpack.storage.disk.DiskCache
import com.sanj.appstarterpack.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class MoviesRepository(
    private val memoryCache: InMemoryCache,
    private val diskCache: DiskCache,
    private val movieApi: MovieApi
) {

    suspend fun getNowInThreatres(
        page: Int,
        region: String,
        refreshCache: Boolean = false
    ): NowInThreatresResponse {
        return if (refreshCache) {
            try {
                withContext(Dispatchers.IO) {
                    movieApi.fetchNowInTheatres(page, region).apply {
                        memoryCache.put(NowInThreatresResponse::class.java, this)
                        diskCache.saveFile(NowInThreatresResponse::class.java, this)
                    }
                }
            } catch (e: java.lang.Exception) {
                throw e
            }

        } else {
            memoryCache.get(NowInThreatresResponse::class.java).await() ?: return try {
                withContext(Dispatchers.IO) {
                    diskCache.readFile(NowInThreatresResponse::class.java).await()
                        ?: movieApi.fetchNowInTheatres(page, region).apply {
                            memoryCache.put(NowInThreatresResponse::class.java, this)
                            diskCache.saveFile(NowInThreatresResponse::class.java, this).await()
                        }
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
}