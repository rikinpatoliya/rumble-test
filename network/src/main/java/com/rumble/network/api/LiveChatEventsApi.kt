package com.rumble.network.api

import com.rumble.network.dto.livechatevents.MessageEventResponse
import com.rumble.network.dto.livechatevents.MuteUserResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LiveChatEventsApi {
    @FormUrlEncoded
    @POST("service.php?name=chat.message.pin")
    suspend fun pinMessage(
        @Field("video_id") videoId: Long,
        @Field("message_id") messageId: Long
    ): Response<MessageEventResponse>

    @FormUrlEncoded
    @POST("service.php?name=chat.message.unpin")
    suspend fun unpinMessage(
        @Field("video_id") videoId: Long,
        @Field("message_id") messageId: Long
    ): Response<MessageEventResponse>

    @FormUrlEncoded
    @POST("service.php?name=moderation.mute")
    suspend fun muteUser(
        @Field("user_to_mute") userId: String,
        @Field("type") muteType: String,
        @Field("entity_type") entityType: String,
        @Field("video") videoId: Long,
        @Field("duration") duration: Int? = null,
    ): Response<MuteUserResponse>
}