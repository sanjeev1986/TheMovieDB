package com.sample.themoviedb.platform

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Connectivity manager abstraction
 */
class NetworkManager(private val application: Application) {

    private val connectivityManager by lazy { application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    /**
     * Return current network status
     */
    @SuppressLint("MissingPermission")
    fun getNetworkStatus(): NetworkStatus {
        return if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let {

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
                    else -> Disconnected()
                }

            } ?: Disconnected()

        } else {
            connectivityManager.activeNetworkInfo?.takeIf { it.isConnected }?.let {
                when (it.type) {
                    ConnectivityManager.TYPE_WIFI -> WIFI(
                        connectivityManager.isActiveNetworkMetered
                    )
                    ConnectivityManager.TYPE_MOBILE -> Mobile(
                        connectivityManager.isActiveNetworkMetered
                    )
                    ConnectivityManager.TYPE_ETHERNET -> Ethernet(
                        connectivityManager.isActiveNetworkMetered
                    )
                    else -> Disconnected()
                }

            } ?: Disconnected()

        }
    }

    fun listenToConnectivityChanges() = flow<NetworkStatus> {
        emit(suspendCoroutine<NetworkStatus> { cont ->
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .build()
            connectivityManager.registerNetworkCallback(
                networkRequest,
                object : ConnectivityManager.NetworkCallback() {
                    override fun onLost(network: Network?) {
                        cont.resume(Disconnected())
                    }

                    override fun onUnavailable() {
                        cont.resume(Disconnected())
                    }

                    override fun onLosing(network: Network?, maxMsToLive: Int) {
                        cont.resume(Disconnected())
                    }

                    override fun onAvailable(network: Network?) {
                        connectivityManager.unregisterNetworkCallback(this)
                        connectivityManager.getNetworkCapabilities(network)?.run {
                            when {
                                hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> cont.resume(
                                    WIFI(
                                        connectivityManager.isActiveNetworkMetered
                                    )
                                )
                                hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> cont.resume(
                                    Mobile(
                                        connectivityManager.isActiveNetworkMetered
                                    )
                                )
                                hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> cont.resume(
                                    Ethernet(
                                        connectivityManager.isActiveNetworkMetered
                                    )
                                )
                            }
                        }
                    }
                }
            )
        })
    }.distinctUntilChanged()

}


sealed class NetworkStatus
data class WIFI(val isMetered: Boolean) : NetworkStatus()
data class Mobile(val isMetered: Boolean) : NetworkStatus()
data class Ethernet(val isMetered: Boolean) : NetworkStatus()
data class Disconnected(val e: Exception = NotConnectedToInternet) : NetworkStatus()

object NotConnectedToInternet : Exception("Not connected to internet")