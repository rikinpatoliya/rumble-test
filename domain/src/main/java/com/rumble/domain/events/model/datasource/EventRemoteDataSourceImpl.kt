package com.rumble.domain.events.model.datasource

import com.rumble.network.api.DebugLogsApi
import com.rumble.network.api.EventApi
import com.rumble.network.dto.events.EventBody
import com.rumble.network.dto.events.EventResponse
import okhttp3.ResponseBody
import retrofit2.Response

class EventRemoteDataSourceImpl(
    private val eventApi: EventApi,
    private val debugLogsApi: DebugLogsApi,
) : EventRemoteDataSource {
    override suspend fun sendWatchProgressEvents(
        eventEndpoint: String,
        eventBody: EventBody
    ): Response<EventResponse> =
        eventApi.postEvent(eventUrl = eventEndpoint, body = eventBody)

    override suspend fun sendAnalyticsEvent(
        userId: String,
        eventBody: EventBody
    ): Response<ResponseBody> =
        debugLogsApi.postEvent(userId = userId, eventBody = eventBody)

    override suspend fun sendDebugAnalyticsEvent(
        userId: String,
        eventName: String,
        eventParams: Map<String, String?>
    ): Response<ResponseBody> =
        debugLogsApi.postAnalyticsEvent(
            userId = userId,
            eventName = eventName,
            params = eventParams
        )
}