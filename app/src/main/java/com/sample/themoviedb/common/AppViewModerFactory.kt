package com.sample.themoviedb.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.api.ApiManager
import com.sample.themoviedb.details.MovieDetailsViewModel
import com.sample.themoviedb.discover.DiscoverViewModel
import com.sample.themoviedb.genres.GenresViewModel
import com.sample.themoviedb.intheatres.InTheatresViewModel
import com.sample.themoviedb.platform.PlatformManager
import com.sample.themoviedb.repositories.RepositoryManager
import com.sample.themoviedb.search.SearchViewModel
import com.sample.themoviedb.storage.StorageManager
import com.sample.themoviedb.trending.TrendingViewModel
import kotlin.reflect.KClass

/**
 * View model Abstract factory
 */
@Suppress("UNCHECKED_CAST")
class AppViewModerFactory(
    private val apiManager: ApiManager,
    private val platformManager: PlatformManager,
    private val storageManager: StorageManager,
    private val repositoryManager: RepositoryManager
) {

    companion object {
        private var testInstanceMap = mutableMapOf<KClass<*>, ViewModelProvider.Factory>()

        /**
         * Set this instance for Espresso testing
         */
        fun setInstance(cls: KClass<*>, mock: ViewModelProvider.Factory) {
            testInstanceMap[cls] = mock
        }
    }


    fun buildInTheatresViewModelFactory(): ViewModelProvider.Factory =
        testInstanceMap.remove(InTheatresViewModel::class) ?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return InTheatresViewModel(
                    storageManager.memoryCache,
                    apiManager.movieApi
                ) as T
            }
        }

    fun buildTrendingViewModelFactory(): ViewModelProvider.Factory =
        testInstanceMap.remove(TrendingViewModel::class) ?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return TrendingViewModel(
                    storageManager.memoryCache,
                    apiManager.trendingApi
                ) as T
            }
        }

    fun buildDiscoverViewModelFactory(): ViewModelProvider.Factory =
        testInstanceMap.remove(DiscoverViewModel::class) ?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DiscoverViewModel(
                    storageManager.memoryCache,
                    apiManager.discoverApi
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
        testInstanceMap.remove(GenresViewModel::class) ?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return GenresViewModel(
                    repositoryManager.genreRepository,
                    platformManager.networkManager
                ) as T
            }
        }

    fun buildMovieDetailsViewModelFactory(movieId: Int): ViewModelProvider.Factory =
        testInstanceMap.remove(MovieDetailsViewModel::class) ?: object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(
                    movieId,
                    repositoryManager.movieDetailsRepository
                ) as T
            }
        }
}
