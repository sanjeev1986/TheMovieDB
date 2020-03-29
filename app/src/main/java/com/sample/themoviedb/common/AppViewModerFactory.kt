package com.sample.themoviedb.common

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.api.ApiManager
import com.sample.themoviedb.browse.intheatres.InTheatresViewModel
import com.sample.themoviedb.search.SearchViewModel
import com.sample.themoviedb.genres.GenresViewModel
import com.sample.themoviedb.repositories.GenreRepository
import com.sanj.appstarterpack.platform.PlatformManager
import com.sanj.appstarterpack.storage.StorageManager
import kotlin.reflect.KClass

/**
 * View model Abstract factory
 */
@Suppress("UNCHECKED_CAST")
class AppViewModerFactory(
    private val application: Application
    ,
    private val apiManager: ApiManager
    ,
    private val platformManager: PlatformManager//not used but added to demonstrate app pattern scalability and ease of extension
    ,
    private val storageManager: StorageManager//not used but added to demonstrate app pattern scalability and ease of extension
) {


    companion object {
        private var testInstanceMap = mutableMapOf<KClass<*>,ViewModelProvider.Factory>()

        /**
         * Set this instance for Espresso testing
         */
        fun setInstance(cls: KClass<*>, mock: ViewModelProvider.Factory) {
            testInstanceMap[cls] = mock
        }
    }


    fun buildBrowseMoviesViewModelFactory(): ViewModelProvider.Factory =
        testInstanceMap.remove(InTheatresViewModel::class) ?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return InTheatresViewModel(
                    apiManager.movieApi
                ) as T
            }
        }

    fun buildSearchViewModelFactory(): ViewModelProvider.Factory =
        testInstanceMap.remove(SearchViewModel::class) ?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(
                    apiManager.searchApi
                ) as T
            }
        }

    fun buildGenreViewModelFactory(): ViewModelProvider.Factory =
        testInstanceMap.remove(GenresViewModel::class )?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GenresViewModel(
                    GenreRepository(
                        platformManager.networkManager,
                        storageManager.memoryCache,
                        storageManager.diskCache,
                        apiManager.genreApi
                    )
                ) as T
            }
        }


}