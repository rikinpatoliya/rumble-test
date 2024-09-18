package com.rumble.network.dto.livevideo

import com.google.gson.annotations.SerializedName

data class LiveReportBody(
    @SerializedName("data")
    val data: LiveReportBodyData
)
