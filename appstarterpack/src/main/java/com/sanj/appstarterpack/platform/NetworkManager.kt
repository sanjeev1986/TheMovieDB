package com.sanj.appstarterpack.platform

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

import android.net.ConnectivityManager
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
        return connectivityManager.activeNetworkInfo?.let {
            if (connectivityManager.activeNetworkInfo.isConnected) {
                Single.just(
                    NetworkStatus(
                        it.isConnected,
                        it.type
                    )
                )
            } else {
                Single.error(NoConnectivity)
            }

        } ?: Single.error(NoConnectivity)
    }



}

object NoConnectivity : Exception()
data class NetworkStatus(val isConnected: Boolean, val networkType: Int)
