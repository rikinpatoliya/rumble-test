package com.rumble.network.interceptors

import com.google.firebase.perf.FirebasePerformance
import com.rumble.network.CustomPerformanceUrlList
import com.rumble.network.NetworkRumbleConstants.CONTENT_TYPE
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class PerformanceInterceptor @Inject constructor() : Interceptor {

    private val queryName = "name"

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (CustomPerformanceUrlList.urlIsInList(request.url)) {
            var performanceUrl =  request.url.scheme + "://" + request.url.host + request.url.encodedPath
            request.url.queryParameter(queryName)?.let {
                performanceUrl = "$performanceUrl/$it"
            }
            val metric = FirebasePerformance.getInstance().newHttpMetric(
                performanceUrl,
                request.method
            )
            return try {
                metric.start()
                request.body?.contentLength()?.let {
                    metric.setRequestPayloadSize(it)
                }
                val response = chain.proceed(request)
                metric.setHttpResponseCode(response.code)
                response.body?.contentLength()?.let {
                    metric.setResponsePayloadSize(it)
                }
                response.header(CONTENT_TYPE)?.let {
                    metric.setResponseContentType(it)
                }
                metric.stop()
                response
            } catch (t: Throwable) {
                metric.stop()
                chain.proceed(request)
            }
        } else {
            return chain.proceed(request)
        }
    }
}