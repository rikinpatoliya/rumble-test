package com.rumble.network.interceptors

import android.os.Build
import com.rumble.network.NetworkRumbleConstants
import com.rumble.network.di.AppName
import com.rumble.network.di.AppVersion
import com.rumble.network.di.OsVersion
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class BannerUserAgentInterceptor @Inject constructor(
    @AppName private val appName: String,
    @AppVersion private val versionName: String,
    @OsVersion private val osVersion: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val userAgent = "${appName}/${versionName} (${Build.MODEL}; $osVersion) okhttp/${okhttp3.OkHttp.VERSION}"
        proceed(request().newBuilder().addHeader(NetworkRumbleConstants.USER_AGENT, userAgent).build())
    }
}