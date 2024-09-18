package com.rumble.network.dto.settings

import com.google.gson.annotations.SerializedName

data class NotificationSettings(
    @SerializedName("media_approved")
    val mediaApproved: Boolean,
    @SerializedName("media_comment")
    val mediaComment: Boolean,
    @SerializedName("comment_replied")
    val commentReplied: Boolean,
    @SerializedName("battle_posted")
    val battlePosted: Boolean,
    @SerializedName("win_money")
    val winMoney: Boolean,
    @SerializedName("video_trending")
    val videoTrending: Boolean,
    @SerializedName("allow_push")
    val allowPush: Boolean,
    @SerializedName("earn")
    val earn: Boolean,
    @SerializedName("video_live")
    val videoLive: Boolean,
    @SerializedName("follow")
    val follow: Boolean,
    @SerializedName("tag")
    val tag: Boolean,
    @SerializedName("comment")
    val comment: Boolean,
    @SerializedName("comment_reply")
    val commentReply: Boolean,
    @SerializedName("new_video")
    val newVideo: Boolean,
)