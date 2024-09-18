package com.rumble.network.dto.events

import com.google.gson.annotations.SerializedName

data class EventDto(
    @SerializedName("e")
    val eventName: String,
    @SerializedName("t")
    val clientTime: Long,
    @SerializedName("u")
    val userId: Long?,
    @SerializedName("d")
    val event: Any
)
