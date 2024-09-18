package com.rumble.battles.subscriptions.presentation

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.sort.NotificationFrequency

interface NotificationsHandler {
    fun onUpdateEmailFrequency(
        channelDetailsEntity: ChannelDetailsEntity,
        notificationFrequency: NotificationFrequency
    )

    fun onEnablePushForLivestreams(channelDetailsEntity: ChannelDetailsEntity, enable: Boolean)
    fun onEnableEmailNotifications(channelDetailsEntity: ChannelDetailsEntity, enable: Boolean)
}