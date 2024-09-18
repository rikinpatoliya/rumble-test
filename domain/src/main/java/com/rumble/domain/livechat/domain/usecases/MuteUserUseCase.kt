package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.MutePeriod
import com.rumble.domain.livechat.domain.domainmodel.MuteUserResult
import com.rumble.domain.livechat.domain.domainmodel.MutedEntityType
import com.rumble.domain.livechat.model.repository.LiveChatRepository
import javax.inject.Inject

class MuteUserUseCase @Inject constructor(
    private val liveChatRepository: LiveChatRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(liveChatMessageEntity: LiveChatMessageEntity, videoId: Long, mutePeriod: MutePeriod): MuteUserResult {
        val type = if (liveChatMessageEntity.channelId != null) MutedEntityType.Channel else MutedEntityType.User
        val result = liveChatRepository.muteUser(userId = liveChatMessageEntity.userName, videoId = videoId, entityType = type, mutePeriod = mutePeriod)
        if (result is MuteUserResult.Failure) {
            rumbleErrorUseCase(result.error)
        } else if (result is MuteUserResult.MuteFailure) {
            rumbleErrorUseCase(result.muteError)
        }
        return result
    }
}