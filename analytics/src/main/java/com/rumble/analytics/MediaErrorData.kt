package com.rumble.analytics

data class MediaErrorData(
    val videoId: Long,
    val videoUrl: String,
    val errorMessage: String,
    val screenId: String,
    val backgroundMode: String,
    val playbackTime: Long,
    val playbackSpeed: Float,
    val volume: Float,
    val quality: String,
    val bitrate: String,
    val target: String,
    val userId: Int?
)