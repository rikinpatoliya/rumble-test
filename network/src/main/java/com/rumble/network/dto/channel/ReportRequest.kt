package com.rumble.network.dto.channel

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("data")
    val data: ReportData,
)