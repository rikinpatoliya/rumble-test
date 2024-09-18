package com.rumble.network.dto.events

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.login.EventUrl

data class EventResponse(
    @SerializedName("e")
    val eventUrl: EventUrl
)
