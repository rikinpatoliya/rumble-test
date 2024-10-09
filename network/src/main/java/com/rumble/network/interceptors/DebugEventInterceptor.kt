package com.rumble.network.interceptors

import com.rumble.network.NetworkRumbleConstants.ERROR_RESPONSE_CODE
import com.rumble.network.subdomain.SyncRumbleSubdomainUseCase
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class DebugEventInterceptor @Inject constructor(
    private val syncRumbleSubdomainUseCase: SyncRumbleSubdomainUseCase,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        if (runBlocking { syncRumbleSubdomainUseCase() }.isNotBlank())
            return chain.proceed(chain.request())
        else return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_2)
            .code(ERROR_RESPONSE_CODE)
            .message("Cannot make debug request with empty subdomain")
            .body("Empty subdomain".toResponseBody())
            .build()
    }
}