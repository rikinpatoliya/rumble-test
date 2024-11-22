package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.sort.NotificationFrequency
import javax.inject.Inject

data class UpdateChannelNotificationsData(
    val pushNotificationsEnabled: Boolean?,
    val emailNotificationsEnabled: Boolean?,
    val notificationFrequency: NotificationFrequency?
)

class UpdateChannelNotificationsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {

    suspend operator fun invoke(
        channelDetailsEntity: CreatorEntity,
        data: UpdateChannelNotificationsData
    ): Result<CreatorEntity> {
        return channelRepository.updateChannelSubscription(
            id = channelDetailsEntity.channelId,
            type = channelDetailsEntity.type,
            action = UpdateChannelSubscriptionAction.SUBSCRIBE,
            data = UpdateChannelNotificationsData(
                pushNotificationsEnabled = data.pushNotificationsEnabled ?: channelDetailsEntity.pushNotificationsEnabled,
                emailNotificationsEnabled = data.emailNotificationsEnabled ?: channelDetailsEntity.emailNotificationsEnabled,
                notificationFrequency = data.notificationFrequency
            )
        )
    }
}