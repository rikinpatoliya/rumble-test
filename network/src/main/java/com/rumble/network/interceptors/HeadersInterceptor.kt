package com.rumble.network.interceptors

import com.rumble.network.NetworkRumbleConstants.COOKIES_HEADER
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeadersInterceptor @Inject constructor(private val sessionManager: SessionManager) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val cookies = runBlocking { sessionManager.cookiesFlow.first() }
        proceed(request().newBuilder().addHeader(COOKIES_HEADER, cookies).build())
    }
}