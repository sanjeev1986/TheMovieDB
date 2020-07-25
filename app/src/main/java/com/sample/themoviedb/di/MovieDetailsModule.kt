package com.sample.themoviedb.di

import androidx.lifecycle.ViewModelProvider
import com.sample.themoviedb.details.MovieDetailsViewModel
import dagger.Binds
import dagger.Module

@Module
abstract class MovieDetailsModule {
    @Binds
    abstract fun provideMovieDetailsViewModelFactory(factory: MovieDetailsViewModel.MovieDetailsViewModelFactory):
            ViewModelProvider.Factory

}