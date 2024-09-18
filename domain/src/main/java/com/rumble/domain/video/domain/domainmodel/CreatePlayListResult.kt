package com.rumble.domain.video.domain.domainmodel

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.videoplayer.player.internal.notification.RumblePlayList

sealed class CreatePlayListResult {
    data class Success(val playList: RumblePlayList, val initialVideo: VideoEntity): CreatePlayListResult()
    object Failure: CreatePlayListResult()
}