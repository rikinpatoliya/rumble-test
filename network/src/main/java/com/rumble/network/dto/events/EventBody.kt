package com.rumble.network.dto.events

import com.google.gson.annotations.SerializedName

data class EventBody(
    @SerializedName("n")
    val appName: String,
    @SerializedName("v")
    val appVersion: String,
    @SerializedName("o")
    val osInfo: String,
    @SerializedName("e")
    val eventList: List<EventDto>
)
