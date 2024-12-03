package com.rumble.domain.livechat.model.datasource.remote

import android.net.Uri
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.rumble.domain.performance.domain.usecase.CreateLiveStreamMetricUseCase
import com.rumble.network.NetworkRumbleConstants.ACCEPT_HEADER
import com.rumble.network.NetworkRumbleConstants.API
import com.rumble.network.NetworkRumbleConstants.APP_REQUEST_NAME
import com.rumble.network.NetworkRumbleConstants.APP_VERSION
import com.rumble.network.NetworkRumbleConstants.COOKIES_HEADER
import com.rumble.network.NetworkRumbleConstants.OS_VERSION
import com.rumble.network.NetworkRumbleConstants.RUMBLE_DEFAULT_API_VERSION
import com.rumble.network.NetworkRumbleConstants.USER_AGENT
import com.rumble.network.api.EmoteApi
import com.rumble.network.api.LiveChatApi
import com.rumble.network.api.LiveChatEventsApi
import com.rumble.network.dto.livechat.EmoteListResponse
import com.rumble.network.dto.livechat.LiveChatEvent
import com.rumble.network.dto.livechat.LiveChatEventType
import com.rumble.network.dto.livechat.LiveChatMessageBody
import com.rumble.network.dto.livechat.LiveChatResponse
import com.rumble.network.dto.livechat.PaymentProofBody
import com.rumble.network.dto.livechat.PaymentProofResponse
import com.rumble.network.dto.livechatevents.MessageEventResponse
import com.rumble.network.dto.livechatevents.MuteUserResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

interface LiveChatRemoteDataSource {
    suspend fun fetchChatEvents(videoId: Long, cookies: String): Flow<LiveChatEvent>
    suspend fun postMessage(chatId: Long, body: LiveChatMessageBody): Response<LiveChatResponse>?
    suspend fun fetchEmoteList(chatId: Long): Response<EmoteListResponse>
    suspend fun postPaymentProof(
        chatId: Long,
        pendingMessageId: Long,
        paymentProofBody: PaymentProofBody
    ): Response<PaymentProofResponse>?

    suspend fun pinMessage(videoId: Long, messageId: Long): Response<MessageEventResponse>
    suspend fun unpinMessage(videoId: Long, messageId: Long): Response<MessageEventResponse>
    suspend fun deleteMessage(chatId: Long, messageId: Long): Response<ResponseBody>?
    suspend fun muteUser(userId: String, muteType: String, entityType: String, videoId: Long, duration: Int?): Response<MuteUserResponse>
}

class LiveChatRemoteDataSourceImpl(
    private val chatEndpoint: String,
    private val liveChatApi: LiveChatApi?,
    private val emoteApi: EmoteApi,
    private val liveChatEventsApi: LiveChatEventsApi,
    private val appName: String,
    private val versionCode: Int,
    private val packageName: String,
    private val appVersion: String,
    private val osVersion: String,
    private val dispatcher: CoroutineDispatcher,
    private val createLiveStreamMetricUseCase: CreateLiveStreamMetricUseCase,
) : LiveChatRemoteDataSource {

    private val dataElement = "data:"
    private val typeElement = "type"
    private val dataOffset = dataElement.length
    private var measured: Boolean = false

    override suspend fun fetchChatEvents(videoId: Long, cookies: String): Flow<LiveChatEvent> = channelFlow {
        val userAgent = "${appName}/${versionCode} okhttp/${okhttp3.OkHttp.VERSION}"
        val chatUrl = "$chatEndpoint/chat/$videoId/stream"
        val chatFullUrl = Uri.parse(chatUrl)
            .buildUpon()
            .appendQueryParameter(USER_AGENT, userAgent)
            .appendQueryParameter(APP_REQUEST_NAME, packageName)
            .appendQueryParameter(APP_VERSION, appVersion)
            .appendQueryParameter(OS_VERSION, osVersion)
            .appendQueryParameter(API, RUMBLE_DEFAULT_API_VERSION)
            .build()
            .toString()

        withContext(dispatcher) {
            val connection = (URL(chatFullUrl).openConnection() as HttpURLConnection).also {
                it.setRequestProperty(ACCEPT_HEADER, "text/event-stream")
                it.setRequestProperty(COOKIES_HEADER, cookies)
                it.doInput = true
                it.connectTimeout = 5 * 1000
                it.readTimeout = 10 * 60 * 60 * 1000
            }
            val input = connection.inputStream.bufferedReader()
            try {
                val metric = createLiveStreamMetricUseCase()
                metric.start()
                connection.connect()
                input.useLines { lines ->
                    lines.forEach { line ->
                        if (line.startsWith(dataElement)) {
                            getLiveChatEvent(line.substring(dataOffset).trim())?.let {
                                if (measured.not()) {
                                    measured = true
                                    metric.setHttpResponseCode(connection.responseCode)
                                    metric.stop()
                                }
                                trySend(it)
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                trySend(LiveChatEvent.LiveChatError(e.message ?: ""))
                this.cancel(CancellationException(message = null, cause = e))
            } finally {
                connection.disconnect()
                input.close()
            }
        }
    }

    override suspend fun postMessage(
        chatId: Long,
        body: LiveChatMessageBody
    ): Response<LiveChatResponse>? =
        liveChatApi?.sendLiveChatMessage(chatId, body)

    override suspend fun fetchEmoteList(chatId: Long): Response<EmoteListResponse> =
        emoteApi.fetchEmoteList(chatId)

    override suspend fun postPaymentProof(
        chatId: Long,
        pendingMessageId: Long,
        paymentProofBody: PaymentProofBody
    ) = liveChatApi?.sendPaymentProof(chatId, pendingMessageId, paymentProofBody)

    override suspend fun pinMessage(videoId: Long, messageId: Long): Response<MessageEventResponse> =
        liveChatEventsApi.pinMessage(videoId = videoId, messageId = messageId)

    override suspend fun unpinMessage(videoId: Long, messageId: Long): Response<MessageEventResponse> =
        liveChatEventsApi.unpinMessage(videoId = videoId, messageId = messageId)

    override suspend fun deleteMessage(chatId: Long, messageId: Long): Response<ResponseBody>? =
        liveChatApi?.deleteMessage(chatId = chatId, messageId = messageId)

    override suspend fun muteUser(userId: String, muteType: String, entityType: String, videoId: Long, duration: Int?): Response<MuteUserResponse> =
        liveChatEventsApi.muteUser(userId = userId, muteType = muteType, entityType = entityType, videoId = videoId, duration = duration)

    private fun getLiveChatEvent(jsonString: String): LiveChatEvent? {
        val gson = Gson()
        val jsonObject = try {
            gson.fromJson(jsonString, JsonObject::class.java)
        } catch (e: JsonParseException) {
            null
        }
        return when (LiveChatEventType.getByValue(jsonObject?.get(typeElement)?.asString)) {
            LiveChatEventType.INIT -> gson.fromJson(
                jsonString,
                LiveChatEvent.LiveChatInitEvent::class.java
            )

            LiveChatEventType.MESSAGE -> gson.fromJson(
                jsonString,
                LiveChatEvent.LiveChatMessageEvent::class.java
            )

            LiveChatEventType.DELETE_MESSAGES, LiveChatEventType.DELETE_NOT_RANT_MESSAGES -> gson.fromJson(
                jsonString,
                LiveChatEvent.LiveChatDeleteMessagesEvent::class.java
            )

            LiveChatEventType.MUTE_USERS -> gson.fromJson(
                jsonString,
                LiveChatEvent.LiveChatMuteUsersEvent::class.java
            )

            LiveChatEventType.PIN_MESSAGE -> gson.fromJson(
                jsonString,
                LiveChatEvent.PinMessageEvent::class.java
            )

            LiveChatEventType.UNPIN_MESSAGE -> gson.fromJson(
                jsonString,
                LiveChatEvent.UnpinMessageEvent::class.java
            )

            LiveChatEventType.LIVE_GATE -> gson.fromJson(
                jsonString,
                LiveChatEvent.LiveGateEvent::class.java
            )

            LiveChatEventType.RAID_CONFIRMED -> gson.fromJson(
                jsonString,
                LiveChatEvent.RaidConfirmedEvent::class.java
            )

            else -> LiveChatEvent.LiveChatError("Unsupported live chat event type")
        }
    }
}