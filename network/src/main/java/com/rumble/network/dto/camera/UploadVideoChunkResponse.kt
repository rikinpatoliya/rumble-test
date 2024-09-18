package com.rumble.network.dto.camera

import com.google.gson.annotations.SerializedName

data class UploadVideoChunkResponse(
    @SerializedName("data")
    val data: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("success")
    val success: Int,
)