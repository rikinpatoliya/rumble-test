package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.DeleteMessageResult
import com.rumble.domain.livechat.model.repository.LiveChatRepository
import javax.inject.Inject

class DeleteMessageUseCase @Inject constructor(
    private val liveChatRepository: LiveChatRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
): RumbleUseCase {
    suspend operator fun invoke(chatId: Long, messageId: Long): DeleteMessageResult {
       val result = liveChatRepository.deleteMessage(chatId = chatId, messageId = messageId)
        if (result is DeleteMessageResult.Failure) {
            result.error?.let {
                rumbleErrorUseCase(it)
            }
        }
        return result
    }
}