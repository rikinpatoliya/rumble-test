package com.rumble.network.interceptors

import com.rumble.network.NetworkRumbleConstants.ACCEPT_HEADER
import com.rumble.network.NetworkRumbleConstants.CONTENT_TYPE
import com.rumble.network.NetworkRumbleConstants.URL_ENCODED_VALUE
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class UrlEncodedInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request().newBuilder()
                .addHeader(ACCEPT_HEADER, URL_ENCODED_VALUE)
                .addHeader(CONTENT_TYPE, URL_ENCODED_VALUE)
                .build()
        )
    }
}