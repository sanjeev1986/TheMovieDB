package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.api.movies.MovieDetailsResponse
import com.sample.themoviedb.api.movies.NowInThreatresResponse
import com.sample.themoviedb.platform.Disconnected
import com.sample.themoviedb.platform.NetworkManager
import com.sample.themoviedb.storage.CacheResult
import com.sample.themoviedb.storage.disk.DiskCache
import com.sample.themoviedb.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MoviesRepository(
    private val networkManager: NetworkManager,
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
                        memoryCache.put(NowInThreatresResponse::class, this)
                        diskCache.saveFile(NowInThreatresResponse::class, this)
                    }
                }
            } catch (e: java.lang.Exception) {
                throw e
            }

        } else {
            return when (val memCacheResult = memoryCache.get(NowInThreatresResponse::class)) {
                is CacheResult.CacheHit -> {
                    memCacheResult.result
                }
                is CacheResult.CacheMiss, is CacheResult.CacheError -> {
                    withContext(Dispatchers.IO) {
                        when (val diskCacheResult =
                            diskCache.readFile(NowInThreatresResponse::class)) {
                            is CacheResult.CacheHit -> {
                                diskCacheResult.result
                            }
                            is CacheResult.CacheMiss, is CacheResult.CacheError -> {
                                when (val nwkStatus = networkManager.getNetworkStatus()) {
                                    is Disconnected -> {
                                        throw nwkStatus.e
                                    }
                                    else -> {
                                        movieApi.fetchNowInTheatres(page, region).apply {
                                            memoryCache.put(NowInThreatresResponse::class, this)
                                            diskCache.saveFile(NowInThreatresResponse::class, this)
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun getMovieDetails(
        movieId: Int,
        refreshCache: Boolean = false
    ): MovieDetailsResponse {
        return if (refreshCache) {
            withContext(Dispatchers.IO) {
                movieApi.fetchMovieDetails(movieId).apply {
                    memoryCache.put(MovieDetailsResponse::class, this)
                    diskCache.saveFile(MovieDetailsResponse::class, this)
                }
            }
        } else {
            return when (val memCacheResult = memoryCache.get(MovieDetailsResponse::class)) {
                is CacheResult.CacheHit -> {
                    memCacheResult.result
                }
                is CacheResult.CacheMiss, is CacheResult.CacheError -> {
                    withContext(Dispatchers.IO) {
                        when (val diskCacheResult =
                            diskCache.readFile(MovieDetailsResponse::class)) {
                            is CacheResult.CacheHit -> {
                                diskCacheResult.result
                            }
                            is CacheResult.CacheMiss, is CacheResult.CacheError -> {
                                when (val nwkStatus = networkManager.getNetworkStatus()) {
                                    is Disconnected -> {
                                        throw nwkStatus.e
                                    }
                                    else -> {
                                        movieApi.fetchMovieDetails(movieId).apply {
                                            memoryCache.put(MovieDetailsResponse::class, this)
                                            diskCache.saveFile(
                                                MovieDetailsResponse::class,
                                                this
                                            )
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
        }
    }
}