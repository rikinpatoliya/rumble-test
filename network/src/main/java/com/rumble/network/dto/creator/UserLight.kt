package com.rumble.network.dto.creator

import com.google.gson.annotations.SerializedName

data class UserLight(
    @SerializedName("id")
    val id: String?,
    @SerializedName("slug")
    val slug: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("thumb")
    val thumb: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("verified_badge")
    val verifiedBadge: Boolean?
)
