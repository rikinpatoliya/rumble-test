package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.MessageModerationResult
import com.rumble.domain.livechat.model.repository.LiveChatRepository
import javax.inject.Inject

class PinMessageUseCase @Inject constructor(
    private val liveChatRepository: LiveChatRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
): RumbleUseCase {
    suspend operator fun invoke(videoId: Long, messageId: Long): MessageModerationResult {
        val result = liveChatRepository.pinMessage(
            videoId = videoId,
            messageId = messageId
        )
        if (result is MessageModerationResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}