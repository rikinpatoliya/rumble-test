package com.rumble.videoplayer.player.config

sealed class StreamStatus {
    object NotStream: StreamStatus()
    object OfflineStream: StreamStatus()
    object LiveStream: StreamStatus()
}
