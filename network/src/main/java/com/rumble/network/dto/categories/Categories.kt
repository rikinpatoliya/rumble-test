package com.rumble.network.dto.categories

import com.google.gson.annotations.SerializedName

data class Categories(
    @SerializedName("primary")
    val primary: VideoCategory?,
    @SerializedName("secondary")
    val secondary: VideoCategory?
)