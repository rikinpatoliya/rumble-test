package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName
import com.rumble.network.deserializer.FeedObjectType
import com.rumble.network.dto.categories.Categories
import com.rumble.network.dto.comments.Comments
import com.rumble.network.dto.livechat.LiveGate
import com.rumble.network.dto.repost.Repost

data class Video(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("thumb")
    val thumbnail: String?,
    @SerializedName("upload_date")
    val uploadDate: String,
    @SerializedName("views")
    val numberOfView: Int,
    @SerializedName("watching_now")
    val watchingNow: Long?,
    @SerializedName("url")
    val url: String,
    @SerializedName("duration")
    val duration: Int,
    @SerializedName("video_width")
    val videoWidth: Int,
    @SerializedName("video_height")
    val videoHeight: Int,
    @SerializedName("livestream_status")
    val livestreamStatus: Int?,
    @SerializedName("live_datetime")
    val liveDateTime: String?,
    @SerializedName("live_streamed_on")
    val liveStreamedOn: String?,
    @SerializedName("videos")
    val videos: List<VideoVariation>,
    @SerializedName("by")
    val videoSource: VideoSource?,
    @SerializedName("rumble_votes")
    val rumbleVotes: RumbleVotes,
    @SerializedName("log")
    val log: RumbleLog,
    @SerializedName("comments")
    val comments: Comments?,
    @SerializedName("categories")
    val categories: Categories?,
    @SerializedName("tags")
    val tags: List<String>?,
    @SerializedName("are_comments_disabled")
    val areCommentsDisabled: Boolean?,
    @SerializedName("video_stats")
    val videoStats: VideoStatus?,
    @SerializedName("related")
    val related: List<Video>?,
    @SerializedName("livestream_has_dvr")
    val livestreamHasDvr: Boolean?,
    @SerializedName("ppv")
    val ppv: Ppv?,
    @SerializedName("is_age_restricted")
    val ageRestricted: Boolean?,
    @SerializedName("is_chat_disabled")
    val liveChatDisabled: Boolean?,
    @SerializedName("watching_progress")
    val watchingProgress: WatchingProgress?,
    @SerializedName("include_transmit_metadata")
    val includeMetadata: Boolean?,
    @SerializedName("availability")
    val availability: String?,
    @SerializedName("live_gate")
    val liveGate: LiveGate?,
    @SerializedName("reposts_count")
    val repostsCount: Int?,
    @SerializedName("user_reposts")
    val userRepostList: List<Repost>?,
    @SerializedName("object_type")
    val objectType: String = FeedObjectType.Video.value
) : FeedItem
