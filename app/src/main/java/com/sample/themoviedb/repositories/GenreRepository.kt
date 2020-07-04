package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.api.genres.GenreApi
import com.sample.themoviedb.api.genres.GenreResponse
import com.sample.themoviedb.platform.Disconnected
import com.sample.themoviedb.platform.NetworkManager
import com.sample.themoviedb.storage.CacheResult
import com.sample.themoviedb.storage.disk.DiskCache
import com.sample.themoviedb.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GenreRepository(
    private val networkManager: NetworkManager,
    private val inMemoryCache: InMemoryCache,
    private val diskCache: DiskCache,
    private val genreApi: GenreApi
) {

    suspend fun fetchGenres(refreshCache: Boolean = false): List<Genre> {
        return if (refreshCache) {
            withContext(Dispatchers.IO) {
                genreApi.fetchGenres().apply {
                    inMemoryCache.put(GenreResponse::class, this)
                    diskCache.saveFile(GenreResponse::class, this)
                }.genres
            }
        } else {
            return inMemoryCache.get<GenreResponse>(GenreResponse::class)?.let { it.genres }
                ?: kotlin.run {
                    withContext(Dispatchers.IO) {
                        when (val diskCacheResult = diskCache.readFile(GenreResponse::class)) {
                            is CacheResult.CacheHit -> {
                                inMemoryCache.put(
                                    GenreResponse::class,
                                    diskCacheResult.result
                                ).genres
                            }
                            is CacheResult.CacheMiss, is CacheResult.CacheError -> {
                                when (val nwkStatus = networkManager.getNetworkStatus()) {
                                    is Disconnected -> {
                                        throw nwkStatus.e
                                    }
                                    else -> {
                                        genreApi.fetchGenres().apply {
                                            inMemoryCache.put(GenreResponse::class, this)
                                            diskCache.saveFile(GenreResponse::class, this)
                                        }.genres
                                    }
                                }
                            }
                        }
                    }
                }

        }
    }
}
