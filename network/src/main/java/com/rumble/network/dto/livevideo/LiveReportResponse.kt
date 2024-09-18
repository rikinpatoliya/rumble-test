package com.rumble.network.dto.livevideo

import com.google.gson.annotations.SerializedName

data class LiveReportResponse(
    @SerializedName("data")
    val data: LiveReportData
)