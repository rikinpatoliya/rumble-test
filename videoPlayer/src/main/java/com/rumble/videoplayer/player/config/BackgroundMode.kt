package com.rumble.videoplayer.player.config

sealed class BackgroundMode {
    object On : BackgroundMode()
    object Off : BackgroundMode()

    override fun toString(): String =
        if (this is On) "On"
        else "Off"
}