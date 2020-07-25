package com.sample.themoviedb.di

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.search.SearchViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class SearchModule {
    @Binds
    abstract fun provideSearchViewModelFactory(factory: SearchViewModel.SearchViewModelFactory):
            ViewModelProvider.Factory
}