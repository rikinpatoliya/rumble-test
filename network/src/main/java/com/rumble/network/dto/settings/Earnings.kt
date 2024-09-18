package com.rumble.network.dto.settings

import com.google.gson.annotations.SerializedName

data class Earnings(
    @SerializedName("uploaded")
    val uploaded: Int,
    @SerializedName("approved")
    val approved: Int,
    @SerializedName("current_balance")
    val currentBalance: Double,
    @SerializedName("cpm")
    val cpm: Double,
    @SerializedName("total")
    val total: Double,
    @SerializedName("rumble")
    val rumble: Double,
    @SerializedName("youtube")
    val youtube: Double,
    @SerializedName("partners")
    val partners: Double,
)