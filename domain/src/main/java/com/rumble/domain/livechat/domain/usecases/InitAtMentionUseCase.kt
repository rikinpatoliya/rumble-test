package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.network.session.SessionManager
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class InitAtMentionUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {
    suspend operator fun invoke(entities: List<LiveChatMessageEntity>): List<LiveChatMessageEntity> =
        entities.map {
            val username = sessionManager.userNameFlow.firstOrNull()
            val regex = "@${username}".toRegex(RegexOption.IGNORE_CASE)
            val match = regex.find(it.message)
            it.copy(atMentionRange = match?.range)
        }

}