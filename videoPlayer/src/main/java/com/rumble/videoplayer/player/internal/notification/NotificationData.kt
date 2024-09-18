package com.rumble.videoplayer.player.internal.notification

import androidx.media3.common.Player
import androidx.media3.session.MediaSession

internal data class NotificationData(
    val mediaSession: MediaSession,
    val player: Player,
    val enableSeekBar: Boolean
)
