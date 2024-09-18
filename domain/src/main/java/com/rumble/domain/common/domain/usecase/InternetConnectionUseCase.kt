package com.rumble.domain.common.domain.usecase

import com.rumble.network.connection.InternetConnectionState
import com.rumble.network.connection.NetworkType
import com.rumble.network.connection.NetworkTypeResolver
import javax.inject.Inject

class InternetConnectionUseCase @Inject constructor(
    private val networkTypeResolver: NetworkTypeResolver,
) {
    operator fun invoke(): InternetConnectionState {
        val networkType = networkTypeResolver.typeOfNetwork()
        return if (networkType == NetworkType.NONE)
            InternetConnectionState.LOST
        else
            InternetConnectionState.CONNECTED
    }
}