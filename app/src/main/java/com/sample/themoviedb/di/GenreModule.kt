package com.sample.themoviedb.di

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.genres.GenresViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class GenreModule {
    @Binds
    abstract fun provideGenreViewModelFactory(factory: GenresViewModel.GenresViewModelFactory):
            ViewModelProvider.Factory
}