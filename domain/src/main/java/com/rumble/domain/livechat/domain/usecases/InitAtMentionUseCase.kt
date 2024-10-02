package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.LiveChatChannelEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class InitAtMentionUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(
        entities: List<LiveChatMessageEntity>,
        channels: List<LiveChatChannelEntity>
    ): List<LiveChatMessageEntity> =
        entities.map { entity ->
            channels.forEach { channel ->
                val channelRegex = "@${channel.username}".toRegex(RegexOption.IGNORE_CASE)
                val channelMatch = channelRegex.find(entity.message)
                if (channelMatch != null){
                    return@map entity.copy(atMentionRange = channelMatch.range)
                }
            }
            val username = sessionManager.userNameFlow.firstOrNull()
            val regex = "@${username}".toRegex(RegexOption.IGNORE_CASE)
            val match = regex.find(entity.message)
            entity.copy(atMentionRange = match?.range)
        }

}