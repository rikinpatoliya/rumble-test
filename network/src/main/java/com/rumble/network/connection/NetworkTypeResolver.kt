package com.rumble.network.connection

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Suppress("DEPRECATION")
@Singleton
class NetworkTypeResolver @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @SuppressLint("MissingPermission")
    fun typeOfNetwork(): NetworkType {
        try {
            val connManager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities =
                    connManager.getNetworkCapabilities(connManager.activeNetwork)
                return if (networkCapabilities == null) {
                    NetworkType.NONE
                } else {
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                        || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE)
                    ) {
                        NetworkType.WI_FI
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        NetworkType.CELLULAR
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_THREAD) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    ) {
                        NetworkType.OTHER
                    } else {
                        NetworkType.NONE
                    }
                }
            } else {
                val activeNetwork = connManager.activeNetworkInfo
                return if (activeNetwork?.isConnectedOrConnecting == true && activeNetwork.isAvailable) {
                    when (activeNetwork.type) {
                        ConnectivityManager.TYPE_WIFI -> NetworkType.WI_FI
                        ConnectivityManager.TYPE_MOBILE -> NetworkType.CELLULAR
                        ConnectivityManager.TYPE_ETHERNET, ConnectivityManager.TYPE_VPN -> NetworkType.OTHER
                        else -> NetworkType.NONE
                    }
                } else {
                    NetworkType.NONE
                }
            }
        } catch (e: Exception) {
            throw ConnectivityError("NetworkTypeResolver error: ${e.message}")
        }
    }
}