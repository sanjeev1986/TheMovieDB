package com.sanj.appstarterpack.platform

import android.app.Application
import com.sanj.appstarterpack.platform.NetworkManager

/**
 * Access all Android platform capabilities like Connectivity, AlarmManager etc using this Facade
 */
class PlatformManager(application: Application) {
    val networkManager by lazy { NetworkManager(application) }
}