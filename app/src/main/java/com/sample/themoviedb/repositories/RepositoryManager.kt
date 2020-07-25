package com.sample.themoviedb.repositories

import com.sample.themoviedb.api.ApiManager
import com.sample.themoviedb.platform.PlatformManager
import com.sample.themoviedb.storage.StorageManager

class RepositoryManager(
    private val apiManager: ApiManager,
    private val storageManager: StorageManager,
    private val platformManager: PlatformManager
) {

    val genreRepository by lazy {
        GenreRepository(
            platformManager.networkManager,
            storageManager.memoryCache,
            storageManager.diskCache,
            apiManager.genreApi
        )
    }

    val movieDetailsRepository by lazy {
        MovieDetailsRepository(
            platformManager.networkManager,
            storageManager.memoryCache,
            apiManager.movieApi
        )
    }
}
