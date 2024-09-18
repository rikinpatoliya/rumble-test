package com.rumble.network.dto.discover

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("title")
    val title: String,
    @SerializedName("slug")
    val path: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("thumbnail")
    val thumbnail: String?,
    @SerializedName("num_viewers")
    val viewersNumber: Long,
)
