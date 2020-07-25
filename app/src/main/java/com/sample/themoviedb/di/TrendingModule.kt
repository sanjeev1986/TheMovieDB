package com.sample.themoviedb.di

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.trending.TrendingViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class TrendingModule {

    @Binds
    abstract fun provideTrendingViewModelFactory(
        factory: TrendingViewModel.TrendingViewModelFactory
    ): ViewModelProvider.Factory
}