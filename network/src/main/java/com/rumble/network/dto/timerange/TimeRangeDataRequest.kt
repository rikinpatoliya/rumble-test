package com.rumble.network.dto.timerange

import com.google.gson.annotations.SerializedName

data class TimeRangeDataRequest(
    @SerializedName("data")
    val data: TimeRangeEntryList
)
