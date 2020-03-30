package com.sample.themoviedb.platform

import android.app.Application
import com.sample.themoviedb.platform.NetworkManager

/**
 * Access all Android platform capabilities like Connectivity, AlarmManager etc using this Facade
 */
class PlatformManager(application: Application) {
    val networkManager by lazy {
        NetworkManager(
            application
        )
    }
}