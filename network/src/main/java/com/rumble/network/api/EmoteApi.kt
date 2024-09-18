package com.rumble.network.api

import com.rumble.network.dto.livechat.EmoteListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface EmoteApi {

    @GET("service.php?name=emote.list")
    suspend fun fetchEmoteList(@Query("chat_id") chatId: Long): Response<EmoteListResponse>
}