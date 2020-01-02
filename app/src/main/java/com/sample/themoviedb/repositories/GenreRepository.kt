package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.genres.GenreApi
import com.sample.themoviedb.api.genres.GenreResponse
import com.sanj.appstarterpack.storage.disk.DiskCache
import com.sanj.appstarterpack.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext

class GenreRepository(
    private val inMemoryCache: InMemoryCache,
    private val diskCache: DiskCache,
    private val genreApi: GenreApi
) {

    suspend fun fetchGenres(refreshCache: Boolean = false): GenreResponse {
        return if (refreshCache) {
            withContext(Dispatchers.IO) {
                try {
                    genreApi.fetchGenres().apply {
                        inMemoryCache.put(GenreResponse::class.java, this)
                        diskCache.saveFile(GenreResponse::class.java, this).await()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }

        } else {
            inMemoryCache.get(GenreResponse::class.java).await()
                ?: return try {
                    withContext(Dispatchers.IO) {
                        diskCache.readFile(GenreResponse::class.java).await()
                            ?: genreApi.fetchGenres().apply {
                                inMemoryCache.put(GenreResponse::class.java, this)
                                diskCache.saveFile(GenreResponse::class.java, this).await()
                            }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    throw e
                }
        }
    }
}