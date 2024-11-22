package com.rumble.battles.subscriptions.presentation

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.sort.NotificationFrequency

interface NotificationsHandler {
    fun onUpdateEmailFrequency(
        channelDetailsEntity: CreatorEntity,
        notificationFrequency: NotificationFrequency
    )

    fun onEnablePushForLivestreams(channelDetailsEntity: CreatorEntity, enable: Boolean)
    fun onEnableEmailNotifications(channelDetailsEntity: CreatorEntity, enable: Boolean)
}