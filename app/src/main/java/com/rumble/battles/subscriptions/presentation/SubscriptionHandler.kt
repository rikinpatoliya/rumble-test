package com.rumble.battles.subscriptions.presentation

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction

interface SubscriptionHandler {
    fun onUpdateSubscription(
        channel: CreatorEntity?,
        action: UpdateChannelSubscriptionAction
    )
    fun onUnfollow(channel: CreatorEntity)
    fun onCancelUnfollow()
    fun onUpdateSubscriptionStatus(channel: CreatorEntity)
}