package com.sanj.appstarterpack.platform

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import io.reactivex.Single

/**
 * Connectivity manager abstraction
 */
class NetworkManager(private val application: Application) {

    private val connectivityManager by lazy { application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    /**
     * Return current network status
     */
    @SuppressLint("MissingPermission")
    fun getNetworkStatus(): Single<NetworkStatus> {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let {
                Single.just<NetworkStatus>(
                    when {
                        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> WIFI(
                            connectivityManager.isActiveNetworkMetered
                        )
                        it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> Mobile(
                            connectivityManager.isActiveNetworkMetered
                        )
                        it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> Ethernet(
                            connectivityManager.isActiveNetworkMetered
                        )
                        else -> Disconnected
                    }
                )
            } ?: Single.just<NetworkStatus>(Disconnected)

        } else {
            connectivityManager.activeNetworkInfo?.takeIf { it.isConnected }?.let {
                Single.just<NetworkStatus>(
                    when (it.type) {
                        ConnectivityManager.TYPE_WIFI -> WIFI(connectivityManager.isActiveNetworkMetered)
                        ConnectivityManager.TYPE_MOBILE -> Mobile(connectivityManager.isActiveNetworkMetered)
                        ConnectivityManager.TYPE_ETHERNET -> Ethernet(connectivityManager.isActiveNetworkMetered)
                        else -> Disconnected
                    }
                )
            } ?: Single.just<NetworkStatus>(Disconnected)

        }
    }
}


sealed class NetworkStatus
data class WIFI(val isMetered: Boolean) : NetworkStatus()
data class Mobile(val isMetered: Boolean) : NetworkStatus()
data class Ethernet(val isMetered: Boolean) : NetworkStatus()
object Disconnected : NetworkStatus()
