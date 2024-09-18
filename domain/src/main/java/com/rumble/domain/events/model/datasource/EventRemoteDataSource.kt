package com.rumble.domain.events.model.datasource

import com.rumble.network.dto.events.EventBody
import com.rumble.network.dto.events.EventResponse
import okhttp3.ResponseBody
import retrofit2.Response

interface EventRemoteDataSource {
    suspend fun sendWatchProgressEvents(eventEndpoint: String, eventBody: EventBody): Response<EventResponse>
    suspend fun sendAnalyticsEvent(userId: String, eventBody: EventBody): Response<ResponseBody>
    suspend fun sendDebugAnalyticsEvent(userId: String, eventName: String, eventParams: Map<String, String>): Response<ResponseBody>
}