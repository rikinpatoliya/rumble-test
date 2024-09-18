package com.rumble.network.dto.referral

import com.google.gson.annotations.SerializedName

data class Referral(
    @SerializedName("id")
    val id: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("thumb")
    val thumb: String?,
    @SerializedName("commission")
    val commission: Double
)