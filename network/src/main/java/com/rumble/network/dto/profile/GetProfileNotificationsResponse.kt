package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.creator.Creator
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
    val user: Creator?,
    @SerializedName("channel")
    val channel: Creator?,
    @SerializedName("video")
    val video: Video?,
)
