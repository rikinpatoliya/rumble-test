package com.rumble.network.interceptors

import com.rumble.network.NetworkRumbleConstants.ACCEPT_HEADER
import com.rumble.network.NetworkRumbleConstants.CONTENT_TYPE
import com.rumble.network.NetworkRumbleConstants.JSON_CONTENT_VALUE
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/*
* Every request expecting to receive JSON in response should send the accept: application/json HTTP header.
* */
class AcceptJsonHeadersInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request().newBuilder()
                .addHeader(ACCEPT_HEADER, JSON_CONTENT_VALUE)
                .addHeader(CONTENT_TYPE, JSON_CONTENT_VALUE)
                .build()
        )
    }
}