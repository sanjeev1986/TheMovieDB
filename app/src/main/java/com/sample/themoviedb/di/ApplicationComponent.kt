package com.sample.themoviedb.di

import android.app.Application
import android.content.Context
import com.sample.themoviedb.details.MovieDetailsFragment
import com.sample.themoviedb.discover.DiscoverFragment
import com.sample.themoviedb.genres.GenresFragment
import com.sample.themoviedb.intheatres.InTheatresFragment
import com.sample.themoviedb.search.SearchFragment
import com.sample.themoviedb.trending.TrendingFragment
import com.sample.themoviedb.watchlist.WatchListFragment
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        GsonModule::class,
        HttpModule::class,
        PlatformModule::class,
        StorageModule::class,
        ApiModule::class,
        ViewModelFactoryModule::class
    ]
)
interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): ApplicationComponent
    }

    fun inject(fragment: DiscoverFragment)
    fun inject(fragment: InTheatresFragment)
    fun inject(fragment: TrendingFragment)
    fun inject(fragment: SearchFragment)
    fun inject(fragment: GenresFragment)
    fun inject(fragment: MovieDetailsFragment)
    fun inject(fragment: WatchListFragment)
}
