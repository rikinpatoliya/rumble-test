package com.rumble.domain.channels.channeldetails.domain.domainmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowStatus(
    val channelId: String,
    val followed: Boolean,
    val isBlocked: Boolean = false,
    val updateAction: () -> UpdateChannelSubscriptionAction = {
        when {
            isBlocked -> UpdateChannelSubscriptionAction.UNBLOCK
            followed -> UpdateChannelSubscriptionAction.UNSUBSCRIBE
            else -> UpdateChannelSubscriptionAction.SUBSCRIBE
        }
    },
) : Parcelable
