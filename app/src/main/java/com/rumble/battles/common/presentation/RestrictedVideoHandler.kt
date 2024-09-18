package com.rumble.battles.common.presentation

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity

interface RestrictedVideoHandler {
    fun onVideoClick(videoEntity: VideoEntity)
    fun onCancelRestricted()
    fun onWatchRestricted(videoEntity: VideoEntity)
}