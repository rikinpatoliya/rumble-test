package com.rumble.network.interceptors

import com.rumble.network.NetworkRumbleConstants.API
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

/*
* By default all api's should use RUMBLE_DEFAULT_API_VERSION, however it is possible that specific
* api request can have different version, therefore it has to be included in specific api request.
* If the api version param is not present in the query, then ApiVersionInterceptor will add
* RUMBLE_DEFAULT_API_VERSION api version query param.
* */
private const val RUMBLE_DEFAULT_API_VERSION = "7"

class ApiVersionInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val request = chain.request()
        val urlBuilder = request.url.newBuilder()
        request.url.query?.let {
            if (it.contains("api=").not()) {
                urlBuilder.addQueryParameter(API, RUMBLE_DEFAULT_API_VERSION)
            }
        }
        val requestBuilder = request.newBuilder().url(urlBuilder.build())
        return chain.proceed(requestBuilder.build())
    }
}