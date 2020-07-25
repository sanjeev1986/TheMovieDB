package com.sample.themoviedb.di

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.discover.DiscoverViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class DiscoverModule {
    
    @Binds
    abstract fun provideDiscoverViewModelFactory(
        factory: DiscoverViewModel.DiscoverViewModelFactory
    ): ViewModelProvider.Factory
}