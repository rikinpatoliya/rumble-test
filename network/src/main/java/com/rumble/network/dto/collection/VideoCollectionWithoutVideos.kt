package com.rumble.network.dto.collection

import com.google.gson.annotations.SerializedName

data class VideoCollectionWithoutVideos(
    @SerializedName("id")
    val id: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("thumb")
    val thumbnail: String?,
    @SerializedName("type")
    val type: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("backsplash")
    val backsplash: String?,
    @SerializedName("videos")
    val videos: Int,
    @SerializedName("rumbles")
    val rumbles: Int,
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("following")
    val following: Int,
    @SerializedName("followed")
    val followed: Boolean,
)
