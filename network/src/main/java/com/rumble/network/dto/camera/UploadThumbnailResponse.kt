package com.rumble.network.dto.camera

import com.google.gson.annotations.SerializedName

data class UploadThumbnailResponse(
    @SerializedName("ref")
    val thumbnail: String,
)