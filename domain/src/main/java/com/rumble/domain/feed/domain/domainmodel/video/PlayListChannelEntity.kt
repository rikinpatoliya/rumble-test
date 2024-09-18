package com.rumble.domain.feed.domain.domainmodel.video

import android.os.Parcelable
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlayListChannelEntity(
    val channelId: String,
    val url: String,
    val title: String,
    val thumbnail: String?,
    val followers: Int,
    val verifiedBadge: Boolean,
    val followStatus: FollowStatus,
) : Parcelable