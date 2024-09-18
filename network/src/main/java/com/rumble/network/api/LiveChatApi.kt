package com.rumble.network.api

import com.rumble.network.dto.livechat.LiveChatMessageBody
import com.rumble.network.dto.livechat.LiveChatResponse
import com.rumble.network.dto.livechat.PaymentProofBody
import com.rumble.network.dto.livechat.PaymentProofResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface LiveChatApi {

    @POST("chat/{chatId}/message")
    suspend fun sendLiveChatMessage(
        @Path("chatId") chatId: Long,
        @Body liveChatMessageBody: LiveChatMessageBody
    ): Response<LiveChatResponse>

    @POST("chat/{chatId}/purchased/{pendingMessageId}")
    suspend fun sendPaymentProof(
        @Path("chatId") chatId: Long,
        @Path("pendingMessageId") pendingMessageId: Long,
        @Body paymentProofBody: PaymentProofBody
    ): Response<PaymentProofResponse>

    @DELETE("chat/{chatId}/message/{messageId}")
    suspend fun deleteMessage(
        @Path("chatId") chatId: Long,
        @Path("messageId") messageId: Long
    ): Response<ResponseBody>
}