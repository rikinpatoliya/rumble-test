package com.rumble.videoplayer.domain.model

sealed class VideoAdTimeCode {
    object None : VideoAdTimeCode()
    object PostRoll : VideoAdTimeCode()
    object PreRoll : VideoAdTimeCode()
    data class SecondsFromStart(val seconds: Long) : VideoAdTimeCode()
    data class SecondsFromEnd(val seconds: Long) : VideoAdTimeCode()
    data class Percentage(val percentage: Int) : VideoAdTimeCode()
}

fun VideoAdTimeCode.playedAtStart(): Boolean {
    return when (this) {
        is VideoAdTimeCode.PreRoll -> true
        is VideoAdTimeCode.SecondsFromStart -> this.seconds == 0L
        is VideoAdTimeCode.Percentage -> this.percentage == 0
        else -> false
    }
}