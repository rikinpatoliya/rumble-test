package com.rumble.network.dto.timerange

import com.google.gson.annotations.SerializedName

data class TimeRangeEntryList(
    @SerializedName("entries")
    val entryList: List<TimeRangeDto>
)
