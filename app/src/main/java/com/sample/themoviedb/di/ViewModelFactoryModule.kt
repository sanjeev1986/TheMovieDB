package com.sample.themoviedb.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.api.discover.DiscoverApi
import com.sample.themoviedb.api.movies.MovieApi
import com.sample.themoviedb.api.search.SearchApi
import com.sample.themoviedb.api.trending.TrendingApi
import com.sample.themoviedb.details.MovieDetailsViewModel
import com.sample.themoviedb.discover.DiscoverViewModel
import com.sample.themoviedb.genres.GenresViewModel
import com.sample.themoviedb.intheatres.InTheatresViewModel
import com.sample.themoviedb.platform.NetworkManager
import com.sample.themoviedb.repositories.GenreRepository
import com.sample.themoviedb.repositories.MovieDetailsRepository
import com.sample.themoviedb.search.SearchViewModel
import com.sample.themoviedb.storage.db.watchlist.WatchListDao
import com.sample.themoviedb.storage.memory.InMemoryCache
import com.sample.themoviedb.trending.TrendingViewModel
import com.sample.themoviedb.watchlist.WatchListViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class ViewModelFactoryModule {

    @Provides
    @Named("Discover")
    fun provideDiscoverViewModelFactory(
        memoryCache: InMemoryCache,
        watchListDao: WatchListDao,
        discoverApi: DiscoverApi
    ) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return DiscoverViewModel(memoryCache, watchListDao, discoverApi) as T
            }
        }

    @Provides
    @Named("InTheatres")
    fun provideInTheatresViewModelFactory(
        memoryCache: InMemoryCache,
        watchListDao: WatchListDao,
        moviesApi: MovieApi
    ) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return InTheatresViewModel(memoryCache, watchListDao, moviesApi) as T
            }
        }

    @Provides
    @Named("Trending")
    fun provideTrendingViewModelFactory(
        memoryCache: InMemoryCache,
        watchListDao: WatchListDao,
        trendingApi: TrendingApi
    ) = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TrendingViewModel(memoryCache, watchListDao, trendingApi) as T
        }
    }

    @Provides
    @Named("Search")
    fun provideSearchViewModelFactory(searchApi: SearchApi, watchListDao: WatchListDao) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return SearchViewModel(searchApi, watchListDao) as T
            }
        }


    @Provides
    @Named("Genres")
    fun provideGenreViewModelFactory(
        genreRepository: GenreRepository,
        networkManager: NetworkManager
    ) = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return GenresViewModel(genreRepository, networkManager) as T
        }
    }


    @Provides
    @Named("Details")
    fun provideMovieDetailsViewModelFactory(movieDetailsRepository: MovieDetailsRepository) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return MovieDetailsViewModel(movieDetailsRepository) as T
            }
        }

    @Provides
    @Named("Watch-List")
    fun provideWatchListViewModelFactory(watchListDao: WatchListDao) =
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return WatchListViewModel(watchListDao) as T
            }
        }
}