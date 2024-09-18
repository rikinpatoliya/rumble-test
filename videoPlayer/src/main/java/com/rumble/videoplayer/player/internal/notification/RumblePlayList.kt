package com.rumble.videoplayer.player.internal.notification

import com.rumble.videoplayer.player.RumbleVideo

data class RumblePlayList(
    val title: String = "",
    val videoList: List<RumbleVideo>,
    val shuffle: Boolean = false,
    val loopPlayList: Boolean = false,
    val type: PlayListType = PlayListType.PlayList
)