package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class VideoSource(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumb")
    val thumbnail: String?,
    @SerializedName("followed")
    val followed: Boolean?,
    @SerializedName("blocked")
    val blocked: Boolean?,
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("verified_badge")
    val verifiedBadge: Boolean?
)
