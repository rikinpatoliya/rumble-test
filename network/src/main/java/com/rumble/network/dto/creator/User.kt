package com.rumble.network.dto.creator

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("picture")
    val picture: String?,
    @SerializedName("followers")
    val userFollowers: Int,
    @SerializedName("verified_badge")
    val verifiedBadge: Boolean,
    @SerializedName("followed")
    val userFollowed: Boolean,
)