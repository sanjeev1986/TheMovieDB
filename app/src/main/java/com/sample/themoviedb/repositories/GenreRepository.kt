package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.genres.Genre
import com.sample.themoviedb.api.genres.GenreApi
import com.sample.themoviedb.api.genres.GenreResponse
import com.sanj.appstarterpack.platform.*
import com.sanj.appstarterpack.storage.disk.DiskCache
import com.sanj.appstarterpack.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.await
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
                try {
                    genreApi.fetchGenres().apply {
                        inMemoryCache.put(GenreResponse::class.java, this)
                        diskCache.saveFile(GenreResponse::class.java, this).await()

                    }.genres
                } catch (e: Exception) {
                    e.printStackTrace()
                    throw e
                }
            }

        } else {
            inMemoryCache.get(GenreResponse::class.java).await()?.genres
                ?: return try {
                    withContext(Dispatchers.IO) {
                        diskCache.readFile(GenreResponse::class.java).await()?.genres
                            ?: kotlin.run {
                                when (networkManager.getNetworkStatus().await()) {
                                    is Disconnected -> emptyList()
                                    else -> genreApi.fetchGenres().apply {
                                        inMemoryCache.put(GenreResponse::class.java, this)
                                        diskCache.saveFile(GenreResponse::class.java, this).await()
                                    }.genres
                                }
                            }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    throw e
                }
        }
    }
}