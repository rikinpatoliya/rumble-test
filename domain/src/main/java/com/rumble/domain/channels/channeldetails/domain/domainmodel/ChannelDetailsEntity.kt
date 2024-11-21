package com.rumble.domain.channels.channeldetails.domain.domainmodel

import android.os.Parcelable
import com.rumble.domain.common.domain.domainmodel.UniqueId
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class ChannelDetailsEntity(
    val channelId: String,
    val channelTitle: String,
    val name: String = "",
    val type: ChannelType,
    val thumbnail: String,
    val backSplash: String,
    val rumbles: Int,
    val followers: Int,
    val following: Int,
    val videoCount: Int,
    var followed: Boolean,
    val blocked: Boolean,
    val pushNotificationsEnabled: Boolean,
    val emailNotificationsEnabled: Boolean,
    val emailNotificationsFrequency: Int,
    val localsCommunityEntity: LocalsCommunityEntity?,
    val latestVideo: VideoEntity?,
    val featuredVideo: VideoEntity?,
    val verifiedBadge: Boolean,
    val channelUrl: String?,
    val watchingNowCount: Int?,
    override val uuid: UUID = UUID.randomUUID(),
) : Parcelable, UniqueId

@Parcelize
data class LocalsCommunityEntity(
    val title: String,
    val description: String,
    val profileImage: String,
    val communityMembers: Int,
    val comments: Int,
    val posts: Int,
    val likes: Int,
    val videoUrl: String,
    val channelUrl: String,
    val showPremiumFlow: Boolean,
    val joinButtonText: String,
) : Parcelable
