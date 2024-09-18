package com.rumble.network.dto.referral

import com.google.gson.annotations.SerializedName

data class Ticket(
    @SerializedName("own")
    val own: Int,
    @SerializedName("referral")
    val referral: Int
)
