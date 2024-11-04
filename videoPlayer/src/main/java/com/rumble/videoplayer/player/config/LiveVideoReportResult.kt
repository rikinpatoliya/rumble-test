package com.rumble.videoplayer.player.config

data class LiveVideoReportResult(
    val watchingNow: Long,
    val statusCode: Int?,
    val isLive: Boolean,
    val hasLiveGate: Boolean = false,
    val videoTimeCode: Long? = null,
    val countDownValue: Int? = null,
    val chatMode: Int,
)
