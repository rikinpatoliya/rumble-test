package com.rumble.battles.videos.presentation

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity

interface VideoOptionsHandler {
    fun onMoreVideoOptionsClicked(videoEntity: VideoEntity, playListId: String = "")
    fun onSaveToPlayList(videoId: Long)
    fun onSaveToWatchLater(videoId: Long)
    fun onRemoveFromPlayList(playListId: String, videoId: Long)
    fun onShare(videUrl: String)
}