package com.rumble.network.dto.repost

import com.google.gson.annotations.SerializedName
import com.rumble.network.deserializer.FeedObjectType
import com.rumble.network.dto.creator.Channel
import com.rumble.network.dto.creator.User
import com.rumble.network.dto.video.FeedItem
import com.rumble.network.dto.video.Video

data class Repost(
    @SerializedName("id")
    val id: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("video")
    val video: Video,
    @SerializedName("user")
    val user: User,
    @SerializedName("channel")
    val channel: Channel?,
    @SerializedName("created_on")
    val createdOn: String,
    @SerializedName("object_type")
    val objectType: String = FeedObjectType.Repost.value
) : FeedItem
