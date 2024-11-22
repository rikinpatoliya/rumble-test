package com.rumble.network.dto.creator

import com.google.gson.annotations.SerializedName

data class Channel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("url")
    val url: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val picture: String?,
    @SerializedName("followers")
    val followers: Long,
    @SerializedName("verified_badge")
    val verifiedBadge: Boolean,
    @SerializedName("followed")
    val followed: Boolean,
)
