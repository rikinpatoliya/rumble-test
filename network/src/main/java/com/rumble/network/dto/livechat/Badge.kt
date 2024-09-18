package com.rumble.network.dto.livechat

import com.google.gson.annotations.SerializedName

data class Badge(
    @SerializedName("icons")
    val icons: BadgeIcons,
    @SerializedName("label")
    val label: BadgeLabel
)
