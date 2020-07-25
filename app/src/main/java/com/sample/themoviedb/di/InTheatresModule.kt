package com.sample.themoviedb.di

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.intheatres.InTheatresViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class InTheatresModule {
    @Binds
    abstract fun provideInTheatresViewModelFactory(
        factory: InTheatresViewModel.InTheatresViewModelFactory
    ): ViewModelProvider.Factory
}