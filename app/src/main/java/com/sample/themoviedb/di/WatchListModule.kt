package com.sample.themoviedb.di

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.watchlist.WatchListViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class WatchListModule {

    @Binds
    abstract fun provideWatchListViewModelFactory(factory: WatchListViewModel.WatchListViewModelFactory):
            ViewModelProvider.Factory
}