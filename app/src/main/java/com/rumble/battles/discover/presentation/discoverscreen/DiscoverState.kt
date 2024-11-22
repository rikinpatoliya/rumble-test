package com.rumble.battles.discover.presentation.discoverscreen

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.discover.domain.domainmodel.CategoryEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.videoplayer.player.RumblePlayer

data class DiscoverState(
    val dontMissPlayer: RumblePlayer? = null,
    val soundOn: Boolean = false,

    val liveNowLoading: Boolean = false,
    val liveNowVideos: List<VideoEntity> = emptyList(),
    val liveNowError: Boolean = false,

    val editorPicksLoading: Boolean = false,
    val editorPicks: List<VideoEntity> = emptyList(),
    val editorPicksError: Boolean = false,

    val featuredChannelsLoading: Boolean = false,
    val featuredChannels: List<CreatorEntity> = emptyList(),
    val featuredChannelsError: Boolean = false,

    val doNotMissItLoading: Boolean = false,
    val doNotMissItVideo: VideoEntity? = null,
    val doNotMissItError: Boolean = false,

    val popularVideosLoading: Boolean = false,
    val popularVideos: List<VideoEntity> = emptyList(),
    val popularVideosError: Boolean = false,

    val categoryListLoading: Boolean = false,
    val categoryList: List<CategoryEntity> = emptyList(),
    val categoryListError: Boolean = false,
)