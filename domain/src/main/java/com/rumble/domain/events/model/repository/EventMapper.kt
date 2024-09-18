package com.rumble.domain.events.model.repository

import com.rumble.network.dto.events.PlayerType
import com.rumble.network.dto.events.WatchProgressEventDto
import com.rumble.videoplayer.player.TimeRangeData
import com.rumble.videoplayer.presentation.UiType

fun TimeRangeData.getWatchProgressEvent(): WatchProgressEventDto =
    WatchProgressEventDto(
        videoId = videoId,
        startPosition = startTime?.let { (it * 1000).toLong() },
        duration = (duration * 1000).toLong(),
        playbackRate = playbackRate?.let { it * 100 }?.toInt(),
        playbackVolume = defineVolumeValue(uiType, playbackVolume),
        isMuted = defineMutedValue(uiType, muted),
        playerType = definePlayerType(uiType ?: UiType.IN_LIST).value,
        fullScreenModel = defineFullScreenMode(uiType)
    )

private fun definePlayerType(uiType: UiType): PlayerType =
    when (uiType) {
        UiType.IN_LIST -> PlayerType.FEED

        UiType.FULL_SCREEN_LANDSCAPE,
        UiType.FULL_SCREEN_PORTRAIT,
        UiType.EMBEDDED -> PlayerType.VIDEO_PAGE

        UiType.TV -> PlayerType.TV_PLAYER

        UiType.DISCOVER -> PlayerType.DISCOVER_PLAYER
    }

private fun defineFullScreenMode(uiType: UiType?): Int? =
    when (uiType) {
        UiType.FULL_SCREEN_LANDSCAPE,
        UiType.FULL_SCREEN_PORTRAIT,
        UiType.TV -> 1

        else -> null
    }

private fun defineMutedValue(uiType: UiType?, muted: Boolean): Int? =
    if (uiType == UiType.TV) null
    else if (muted) 1
    else 0

private fun defineVolumeValue(uiType: UiType?, playbackVolume: Int): Int? =
    if (uiType == UiType.TV) null
    else playbackVolume
