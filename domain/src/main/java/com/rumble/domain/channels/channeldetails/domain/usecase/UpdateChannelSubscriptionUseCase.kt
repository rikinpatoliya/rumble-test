package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.analytics.FollowRequestSentEvent
import com.rumble.analytics.UnfollowRequestSentEvent
import com.rumble.domain.analytics.domain.usecases.AnalyticsEventUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.domain.channels.model.repository.ChannelRepository
import javax.inject.Inject

class UpdateChannelSubscriptionUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val analyticsEventUseCase: AnalyticsEventUseCase
) {

    suspend operator fun invoke(
        channelDetailsEntity: CreatorEntity,
        action: UpdateChannelSubscriptionAction,
    ): Result<CreatorEntity> {
        if (action == UpdateChannelSubscriptionAction.SUBSCRIBE) {
            analyticsEventUseCase(FollowRequestSentEvent)
        } else if (action == UpdateChannelSubscriptionAction.UNSUBSCRIBE) {
            analyticsEventUseCase(UnfollowRequestSentEvent)
        }
        return channelRepository.updateChannelSubscription(
            id = channelDetailsEntity.channelId,
            type = channelDetailsEntity.type,
            action = action,
        )
    }
}