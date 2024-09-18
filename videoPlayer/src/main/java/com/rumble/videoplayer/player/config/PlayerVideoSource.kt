package com.rumble.videoplayer.player.config

import kotlin.math.abs

data class PlayerVideoSource(
    val videoUrl: String,
    val type: String,
    val resolution: Int,
    val bitrate: Int,
    val qualityText: String?,
    val bitrateText: String?
) {
    fun getResolutionDistanceFrom(videoResolution: Int) =
        abs(1.0 / resolution - 1.0 / videoResolution)

    fun getBitrateDistanceFrom(videoBitrate: Int) =
        abs(bitrate - videoBitrate)
}