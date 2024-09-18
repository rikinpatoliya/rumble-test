package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.UserState

data class UploadChannelsResponse(
    @SerializedName("user")
    val user: UserState,
    @SerializedName("data")
    val data: List<UserUploadChannel>
)

data class UserUploadChannel(
    @SerializedName("id")
    val id: Long,
    @SerializedName("title")
    val title: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("num_subscribers")
    val subscribers: Int,
    @SerializedName("thumb")
    val thumbnail: String?,
)