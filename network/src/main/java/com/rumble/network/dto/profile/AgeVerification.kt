package com.rumble.network.dto.profile

import com.google.gson.annotations.SerializedName

data class AgeVerification(
    @SerializedName("required")
    val required: Boolean,
    @SerializedName("min_eligible")
    val min_eligible: Int
)