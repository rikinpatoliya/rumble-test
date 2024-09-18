package com.rumble.videoplayer.player.config

internal sealed class PlayerPlaybackState(open var isBuffering: Boolean) {
    data class Idle(override var isBuffering: Boolean = false): PlayerPlaybackState(false)
    data class  Fetching(override var isBuffering: Boolean = false): PlayerPlaybackState(false)
    data class  Paused(override var isBuffering: Boolean): PlayerPlaybackState(false)
    data class  Playing(override var isBuffering: Boolean): PlayerPlaybackState(false)
    data class  Finished(override var isBuffering: Boolean = false): PlayerPlaybackState(false)
    data class  PlayerPlaybackReleased(override var isBuffering: Boolean = false): PlayerPlaybackState(false)
    data class Error(override var isBuffering: Boolean = false): PlayerPlaybackState(false)
}