package com.rumble.domain.livechat.model.repository

import com.rumble.analytics.IAP_FAILED
import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.livechat.domain.domainmodel.DeleteMessageResult
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageResult
import com.rumble.domain.livechat.domain.domainmodel.LiveChatResult
import com.rumble.domain.livechat.domain.domainmodel.MessageModerationResult
import com.rumble.domain.livechat.domain.domainmodel.MutePeriod
import com.rumble.domain.livechat.domain.domainmodel.MuteUserResult
import com.rumble.domain.livechat.domain.domainmodel.MutedEntityType
import com.rumble.domain.livechat.domain.domainmodel.PaymentProofResult
import com.rumble.domain.livechat.domain.domainmodel.PendingMessageInfo
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.domain.livechat.model.LiveChatNetworkModelMapper
import com.rumble.domain.livechat.model.datasource.remote.LiveChatRemoteDataSource
import com.rumble.domain.livechat.model.toEmoteEntityList
import com.rumble.domain.livechat.model.toEmoteGroupList
import com.rumble.network.dto.livechat.LiveChatBodyData
import com.rumble.network.dto.livechat.ErrorResponse
import com.rumble.network.dto.livechat.LiveChatMessageBody
import com.rumble.network.dto.livechat.LiveChatMessageRant
import com.rumble.network.dto.livechat.LiveChatMessageText
import com.rumble.network.dto.livechat.PaymentProofBody
import com.rumble.network.dto.livechat.PaymentProofData
import com.rumble.network.queryHelpers.MuteType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Converter
import java.util.UUID

private const val TAG = "LiveChatRepositoryImpl"

class LiveChatRepositoryImpl(
    private val remoteDataSource: LiveChatRemoteDataSource,
    private val errorConverter: Converter<ResponseBody, ErrorResponse>?,
    private val baseUrl: String,
    private val dispatcher: CoroutineDispatcher,
) : LiveChatRepository {

    override suspend fun fetchChatEvents(videoId: Long, currentUserId: String, cookies: String): Flow<LiveChatResult> =
        remoteDataSource.fetchChatEvents(videoId, cookies)
            .map {
                var liveChatResult = LiveChatNetworkModelMapper.mapToLiveChatResult(
                    it,
                    baseUrl,
                    currentUserId
                )
                liveChatResult.liveChatConfig?.let { config ->
                    val emoteListResult = remoteDataSource.fetchEmoteList(config.chatId)
                    if (emoteListResult.isSuccessful) {
                        emoteListResult.body()?.let { response ->
                            liveChatResult = liveChatResult
                                .copy(liveChatConfig = config.copy(
                                    emoteList = response.data.toEmoteEntityList(),
                                    emoteGroups = response.data.toEmoteGroupList(),
                                ))
                        }
                    }
                }
                liveChatResult
            }

    override suspend fun postMessage(
        chatId: Long,
        message: String,
        authorChannelId: Long?,
        rantLevel: RantLevel?
    ): LiveChatMessageResult = withContext(dispatcher) {
        val requestId = UUID.randomUUID().toString()
        val messageBody = LiveChatMessageBody(
            bodyData = LiveChatBodyData(
                requestId = requestId,
                message = LiveChatMessageText(text = message),
                channelId = authorChannelId,
                rant = rantLevel?.rantId?.let { LiveChatMessageRant(it) }
            )
        )
        val response = remoteDataSource.postMessage(chatId, messageBody)
        val chatMessageResult = if (response?.isSuccessful == true) {
            response.body()?.data?.pendingMessageId?.let {
                LiveChatMessageResult.RantMessageSuccess(PendingMessageInfo(chatId, requestId, it))
            } ?: LiveChatMessageResult.MessageSuccess
        } else {
            response?.errorBody()?.let {
                val error = errorConverter?.convert(it)
                val errorMessage = error?.errors?.firstOrNull()?.message ?: ""
                LiveChatMessageResult.Failure(
                    userErrorMessage = errorMessage,
                    rumbleError = RumbleError(TAG, response.raw())
                )
            } ?: LiveChatMessageResult.Failure(
                rumbleError = if (response != null) RumbleError(TAG, response.raw()) else null
            )
        }
        chatMessageResult
    }

    override suspend fun postPaymentProof(
        pendingMessageInfo: PendingMessageInfo,
        token: String,
        appId: String,
        appsFlyerId: String,
    ): PaymentProofResult = withContext(dispatcher) {
        val body = PaymentProofBody(
            paymentProofData = PaymentProofData(
                requestId = pendingMessageInfo.requestId,
                purchaseToken = token,
                packageName = appId,
                installationId = appsFlyerId
            )
        )
        val response = remoteDataSource.postPaymentProof(
            pendingMessageInfo.chatId,
            pendingMessageInfo.pendingMessageId,
            body
        )
        if (response?.isSuccessful == true) PaymentProofResult.Success
        else PaymentProofResult.Failure(response?.raw()?.let { RumbleError(IAP_FAILED, it) })
    }

    override suspend fun pinMessage(videoId: Long, messageId: Long): MessageModerationResult = withContext(dispatcher) {
        val response = remoteDataSource.pinMessage(videoId = videoId, messageId = messageId)
        if (response.isSuccessful) MessageModerationResult.Success
        else MessageModerationResult.Failure(RumbleError(TAG, response.raw()))
    }

    override suspend fun unpinMessage(videoId: Long, messageId: Long): MessageModerationResult = withContext(dispatcher) {
        val response = remoteDataSource.unpinMessage(videoId = videoId, messageId = messageId)
        if (response.isSuccessful) MessageModerationResult.Success
        else MessageModerationResult.Failure(RumbleError(TAG, response.raw()))
    }

    override suspend fun deleteMessage(chatId: Long, messageId: Long): DeleteMessageResult = withContext(dispatcher) {
        val response = remoteDataSource.deleteMessage(chatId = chatId, messageId = messageId)
        if (response?.isSuccessful == true) DeleteMessageResult.Success
        else DeleteMessageResult.Failure(response?.let { RumbleError(TAG, it.raw()) })
    }

    override suspend fun muteUser(userId: String, videoId: Long, mutePeriod: MutePeriod, entityType: MutedEntityType): MuteUserResult = withContext(dispatcher) {
        val type: MuteType = when (mutePeriod) {
            MutePeriod.FiveMinutes, MutePeriod.LiveStreamDuration -> MuteType.Video
            MutePeriod.Forever -> MuteType.Total
        }
        val response = remoteDataSource.muteUser(
            userId = userId,
            muteType = type.value,
            entityType = entityType.value,
            videoId = videoId,
            duration = mutePeriod.duration
        )
        if (response.isSuccessful && response.body()?.data?.success == true) MuteUserResult.Success
        else {
            response.errorBody()?.let {
                val error = errorConverter?.convert(it)
                val errorMessage = error?.errors?.firstOrNull()?.message ?: ""
                MuteUserResult.MuteFailure(
                    errorMessage = errorMessage,
                    muteError = RumbleError(TAG, response.raw())
                )
            } ?: run {
                MuteUserResult.Failure(RumbleError(TAG, response.raw()))
            }
        }
    }
}