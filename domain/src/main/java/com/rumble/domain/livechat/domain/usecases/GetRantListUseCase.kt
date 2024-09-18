package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.RantConfig
import com.rumble.domain.livechat.domain.domainmodel.RantEntity
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class GetRantListUseCase @Inject constructor() {

    operator fun invoke(
        messageList: List<LiveChatMessageEntity>,
        rantConfig: RantConfig
    ): List<RantEntity> =
        messageList
            .filter { it.rantPrice != null && it.deleted.not() }
            .map {
                RantEntity(
                    messageEntity = it,
                    timeLeftPercentage = calculateTimeLeft(it, rantConfig)
                )
            }
            .filter { it.timeLeftPercentage > 0 }

    private fun calculateTimeLeft(message: LiveChatMessageEntity, rantConfig: RantConfig): Float =
        rantConfig.levelList.find { it.rantPrice.compareTo(message.rantPrice) == 0 }?.let {
            val timeFromStart = ChronoUnit.SECONDS.between(message.timeReceived, LocalDateTime.now())
            val timeLeft = it.duration - timeFromStart
            if (timeLeft < 0L) 0f
            else timeLeft / it.duration.toFloat()
        } ?: 0f
}