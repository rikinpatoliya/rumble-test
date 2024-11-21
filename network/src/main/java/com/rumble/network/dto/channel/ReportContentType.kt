package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName


enum class ReportContentType {
    @SerializedName("video")
    VIDEO,

    @SerializedName("channel")
    CHANNEL,

    @SerializedName("comment")
    COMMENT,

    @SerializedName("user")
    USER,

    @SerializedName("repost")
    REPOST
}
