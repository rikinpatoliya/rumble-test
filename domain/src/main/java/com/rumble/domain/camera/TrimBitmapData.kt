package com.rumble.domain.camera

data class TrimBitmapData(
    val height: Int,
    val width: Int,
    val quantity: Int,
    val uploadVideoInfo: UploadVideoInfo,
    val thumbExtractTimes: List<Long>
)