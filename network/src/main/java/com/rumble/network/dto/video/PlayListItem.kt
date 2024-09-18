package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class PlayListItem(
    @SerializedName("created_on")
    val createdOn: String?,
    @SerializedName("video")
    val video: Video,
)