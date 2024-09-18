package com.rumble.network.api

import com.rumble.network.dto.events.EventBody
import com.rumble.network.dto.events.EventResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface EventApi {
    @POST
    suspend fun postEvent(@Url eventUrl: String, @Body body: EventBody): Response<EventResponse>
}