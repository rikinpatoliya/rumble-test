package com.rumble.domain.livechat.model.repository

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
import kotlinx.coroutines.flow.Flow

interface LiveChatRepository {
    suspend fun fetchChatEvents(videoId: Long): Flow<LiveChatResult>
    suspend fun postMessage(
        chatId: Long,
        message: String,
        authorChannelId: Long?,
        rantLevel: RantLevel?
    ): LiveChatMessageResult

    suspend fun postPaymentProof(
        pendingMessageInfo: PendingMessageInfo,
        token: String,
        appId: String,
        appsFlyerId: String,
    ): PaymentProofResult

    suspend fun pinMessage(
        videoId: Long,
        messageId: Long,
    ): MessageModerationResult

    suspend fun unpinMessage(
        videoId: Long,
        messageId: Long,
    ): MessageModerationResult

    suspend fun deleteMessage(
        chatId: Long,
        messageId: Long,
    ): DeleteMessageResult

    suspend fun muteUser(
        userId: String,
        videoId: Long,
        mutePeriod: MutePeriod,
        entityType: MutedEntityType
    ): MuteUserResult
}