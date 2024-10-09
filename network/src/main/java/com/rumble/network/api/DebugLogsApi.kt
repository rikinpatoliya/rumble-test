package com.rumble.network.api

import com.rumble.network.dto.events.EventBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface DebugLogsApi {
    @POST("debug/log2")
    suspend fun postEvent(@Query("uid") userId: String, @Body eventBody: EventBody): Response<ResponseBody>

    @POST("debug/log2")
    suspend fun postAnalyticsEvent(@Query("uid") userId: String, @Query("event") eventName: String, @QueryMap params: Map<String, String>): Response<ResponseBody>
}