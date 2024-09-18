package com.rumble.battles.subscriptions.presentation

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction

interface SubscriptionHandler {
    fun onUpdateSubscription(
        channel: ChannelDetailsEntity?,
        action: UpdateChannelSubscriptionAction
    )
    fun onUnfollow(channel: ChannelDetailsEntity)
    fun onCancelUnfollow()
    fun onUpdateSubscriptionStatus(channel: ChannelDetailsEntity)
}