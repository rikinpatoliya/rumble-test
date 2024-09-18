package com.rumble.utils.extension

import android.net.http.HttpException
import android.os.Build

fun Throwable.isNetworkRelatedError(): Boolean =
    when {
        this is retrofit2.HttpException ||
                this is java.io.IOException ||
                this is java.net.UnknownHostException ||
                this is java.net.SocketTimeoutException ||
                this is java.net.ConnectException ||
                this is java.net.NoRouteToHostException ||
                this is javax.net.ssl.SSLException ||
                this is java.nio.channels.ClosedChannelException ||
                this is java.nio.channels.UnresolvedAddressException ||
                this is android.system.ErrnoException -> true
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE && this is HttpException -> true
        else -> false
    }