package com.rumble.network.interceptors

import com.rumble.network.NetworkRumbleConstants.APP_REQUEST_NAME
import com.rumble.network.NetworkRumbleConstants.APP_VERSION
import com.rumble.network.NetworkRumbleConstants.OS_VERSION
import com.rumble.network.di.AppRequestName
import com.rumble.network.di.AppVersion
import com.rumble.network.di.OsVersion
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class QueryInterceptor @Inject constructor(
    @AppRequestName private val appRequestName: String,
    @AppVersion private val appVersion: String,
    @OsVersion private val osVersion: String,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val request = chain.request()
        val urlBuilder = request.url.newBuilder()
        urlBuilder.addQueryParameter(APP_VERSION, appVersion)
        urlBuilder.addQueryParameter(APP_REQUEST_NAME, appRequestName)
        urlBuilder.addQueryParameter(OS_VERSION, osVersion)
        val requestBuilder = request.newBuilder().url(urlBuilder.build())

        return chain.proceed(requestBuilder.build())
    }
}