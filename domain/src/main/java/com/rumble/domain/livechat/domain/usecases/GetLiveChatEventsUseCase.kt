package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.LiveChatResult
import com.rumble.domain.livechat.model.repository.LiveChatRepository
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLiveChatEventsUseCase @Inject constructor(
    private val liveChatRepository: LiveChatRepository,
    private val sessionManager: SessionManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(videoId: Long): Flow<LiveChatResult> {
        val userId = sessionManager.userIdFlow.first()
        val cookies = sessionManager.cookiesFlow.first()
        return liveChatRepository.fetchChatEvents(videoId, userId, cookies).map {
            if (it.success.not()) { rumbleErrorUseCase(it.rumbleError) }
            it
        }
    }

}