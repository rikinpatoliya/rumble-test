package com.rumble.network.dto.categories

import com.google.gson.annotations.SerializedName

data class VideoCategory(
    @SerializedName("slug")
    val slug: String,
    @SerializedName("title")
    val title: String
)