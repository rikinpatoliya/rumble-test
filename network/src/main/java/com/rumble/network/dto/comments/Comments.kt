package com.rumble.network.dto.comments

import com.google.gson.annotations.SerializedName

data class Comments(
    @SerializedName("count")
    val count: Int,
    @SerializedName("items")
    val items: List<Comment>?
)
