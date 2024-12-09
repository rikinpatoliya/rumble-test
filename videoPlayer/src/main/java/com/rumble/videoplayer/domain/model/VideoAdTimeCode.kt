package com.rumble.videoplayer.domain.model

sealed class VideoAdTimeCode {
    data object None : VideoAdTimeCode()
    data object PostRoll : VideoAdTimeCode()
    data object PreRoll : VideoAdTimeCode()
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