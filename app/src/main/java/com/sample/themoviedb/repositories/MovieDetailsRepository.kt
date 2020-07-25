package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.api.movies.MovieDetailsResponse
import com.sample.themoviedb.platform.Disconnected
import com.sample.themoviedb.platform.NetworkManager
import com.sample.themoviedb.storage.memory.InMemoryCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieDetailsRepository @Inject constructor(
    private val networkManager: NetworkManager,
    private val inMemoryCache: InMemoryCache,
    private val movieApi: MovieApi
) {
    suspend fun fetchMovieDetails(movieId: Int): MovieDetailsResponse {
        return inMemoryCache[MovieDetailsRepository::class.simpleName!! + movieId]
            ?: when (
                val nwkStatus =
                    networkManager.getNetworkStatus()
                ) {
                is Disconnected -> {
                    throw nwkStatus.e
                }
                else -> {
                    withContext(Dispatchers.IO) {
                        movieApi.fetchMovieDetails(movieId)
                    }.also {
                        inMemoryCache.put(MovieDetailsRepository::class.simpleName!! + movieId, it)
                    }
                }
            }
    }
}
