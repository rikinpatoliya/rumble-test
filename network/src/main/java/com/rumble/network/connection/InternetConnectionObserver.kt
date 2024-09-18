package com.rumble.network.connection

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.rumble.network.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("MissingPermission")
@Singleton
class InternetConnectionObserver @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val networkTypeResolver: NetworkTypeResolver,
) {
    private val scope = CoroutineScope(ioDispatcher)
    private var callback : ConnectivityManager.NetworkCallback
    private val connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val connectivityFlow: MutableSharedFlow<InternetConnectionState> = MutableSharedFlow()

    init {
        callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                scope.launch {
                    connectivityFlow.emit(InternetConnectionState.CONNECTED)
                }
            }

            override fun onLost(network: Network) {
                scope.launch {
                    if (networkTypeResolver.typeOfNetwork() == NetworkType.NONE) {
                        connectivityFlow.emit(InternetConnectionState.LOST)
                    }
                }
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        try {
            connectivityManager.registerNetworkCallback(networkRequest, callback)
        } catch (e: Exception) {
            throw ConnectivityError("InternetConnectionObserver error: ${e.message}")
        }
    }
}