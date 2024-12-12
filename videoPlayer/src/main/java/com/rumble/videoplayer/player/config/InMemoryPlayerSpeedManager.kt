package com.rumble.videoplayer.player.config

internal object InMemoryPlayerSpeedManager {
    private val currentSpeed: MutableMap<VideoScope, PlaybackSpeed> = mutableMapOf()

    init {
        currentSpeed[VideoScope.VideoDetails] = PlaybackSpeed.NORMAL
        currentSpeed[VideoScope.Other] = PlaybackSpeed.NORMAL
    }

    fun getPlayerSpeed(videoScope: VideoScope): PlaybackSpeed =
        currentSpeed[videoScope] ?: PlaybackSpeed.NORMAL

    fun setPlayerSpeed(videoScope: VideoScope, speed: PlaybackSpeed) {
        currentSpeed[videoScope] = speed
    }

    fun resetSpeedValues() {
        currentSpeed.entries.forEach {
            it.setValue(PlaybackSpeed.NORMAL)
        }
    }
}