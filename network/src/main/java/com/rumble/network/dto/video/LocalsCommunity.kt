package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName

data class LocalsCommunity(
    @SerializedName("owner_name")
    val ownerName: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("logo_url")
    val logoUrl: String,
    @SerializedName("urls")
    val urls: Urls,
    @SerializedName("counts")
    val counts: Counts
)