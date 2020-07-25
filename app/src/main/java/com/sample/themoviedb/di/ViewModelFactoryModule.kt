package com.sample.themoviedb.di

import dagger.Module

@Module(
    includes = [DiscoverModule::class,
        InTheatresModule::class,
        TrendingModule::class,
        SearchModule::class,
        GenreModule::class,
        MovieDetailsModule::class,
        WatchListModule::class]
)
abstract class ViewModelFactoryModule