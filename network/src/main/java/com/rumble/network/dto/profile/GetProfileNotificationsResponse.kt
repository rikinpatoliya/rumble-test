package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.channel.Channel
import com.rumble.network.dto.login.UserState
import com.rumble.network.dto.video.Video

data class GetProfileNotificationsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: ProfileNotificationsData
)

data class ProfileNotificationsData(
    @SerializedName("items")
    val notificationsList: List<ProfileNotificationItem>?
)

data class ProfileNotificationItem(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: RumbleNotificationType,
    @SerializedName("sent_on")
    val sentOn: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("tag")
    val tag: Int?,
    @SerializedName("user")
    val user: Channel?,
    @SerializedName("channel")
    val channel: Channel?,
    @SerializedName("video")
    val video: Video?,
)
