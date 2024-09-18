package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class Counts(
    @SerializedName("likes")
    val likes: Int,
    @SerializedName("posts")
    val posts: Int,
    @SerializedName("members")
    val members: Int,
    @SerializedName("comments")
    val comments: Int
)