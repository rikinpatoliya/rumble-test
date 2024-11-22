package com.rumble.domain.feed.domain.domainmodel.video

import android.os.Parcelable
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserEntity(
    val id: String = "",
    val username: String = "",
    val thumbnail: String? = null,
    val followers: Int = 0,
    val verifiedBadge: Boolean = false,
    val followStatus: FollowStatus = FollowStatus("", false, false),
) : Parcelable