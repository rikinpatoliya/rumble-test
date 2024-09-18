package com.rumble.network.dto.camera

import com.google.gson.annotations.SerializedName

data class MergeVideoChunksResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("success")
    val success: Int,
)