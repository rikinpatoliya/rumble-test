package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageResult
import com.rumble.domain.livechat.domain.domainmodel.RantLevel
import com.rumble.domain.livechat.model.repository.LiveChatRepository
import javax.inject.Inject

class PostLiveChatMessageUseCase @Inject constructor(
    private val liveChatRepository: LiveChatRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke(
        chatId: Long,
        message: String,
        authorChannelId: Long?,
        rantLevel: RantLevel? = null
    ): LiveChatMessageResult {
        val result =
            liveChatRepository.postMessage(
                chatId = chatId,
                message = message,
                authorChannelId = authorChannelId,
                rantLevel = rantLevel
            )
        if (result is LiveChatMessageResult.Failure) rumbleErrorUseCase(result.rumbleError)
        return result
    }
}