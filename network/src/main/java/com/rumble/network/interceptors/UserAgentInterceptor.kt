package com.rumble.network.interceptors

import com.rumble.network.NetworkRumbleConstants.USER_AGENT
import com.rumble.network.di.AppName
import com.rumble.network.di.VersionCode
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UserAgentInterceptor @Inject constructor(
    @AppName private val appName: String,
    @VersionCode private val versionCode: String
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val userAgent = "${appName}/${versionCode} okhttp/${okhttp3.OkHttp.VERSION}"
        proceed(request().newBuilder().addHeader(USER_AGENT, userAgent).build())
    }
}