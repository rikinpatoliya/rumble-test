package com.rumble.videoplayer.player.config

internal sealed class AdPlaybackState {
    object None : AdPlaybackState()
    object Buffering: AdPlaybackState()
    object Resumed : AdPlaybackState()
    object Paused : AdPlaybackState()
}