package com.rumble.videoplayer.player

import com.rumble.videoplayer.presentation.UiType

data class TimeRangeData(
    val videoId: Long = 0,
    val startTime: Float? = null,
    val duration: Float = 0f,
    val isPlaceholder: Boolean = false,
    val playbackRate: Float? = null,
    val playbackVolume: Int = 0,
    val muted: Boolean = false,
    val uiType: UiType? = null,
)
