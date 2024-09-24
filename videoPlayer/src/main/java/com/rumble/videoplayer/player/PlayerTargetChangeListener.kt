package com.rumble.videoplayer.player

import com.rumble.videoplayer.player.config.PlayerTarget

interface PlayerTargetChangeListener {
    fun onPlayerTargetChanged(currentTarget: PlayerTarget)
}