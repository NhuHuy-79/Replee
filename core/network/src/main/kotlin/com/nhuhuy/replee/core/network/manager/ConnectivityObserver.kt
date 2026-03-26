package com.nhuhuy.replee.core.network.manager

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

sealed class NetworkStatus {
    data object Online : NetworkStatus()
    data object Offline : NetworkStatus()
}


class ConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun observe(): Flow<NetworkStatus> = callbackFlow {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val callback = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Online)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Offline)
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Offline)
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }

    }.distinctUntilChanged()
}