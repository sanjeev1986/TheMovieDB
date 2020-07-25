package com.sample.themoviedb

import com.sample.themoviedb.di.ApplicationComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component()
interface TestAppComponent : ApplicationComponent
