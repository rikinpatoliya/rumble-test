package com.rumble.domain.timerange.model

import com.rumble.domain.timerange.model.datasource.local.RoomTimeRange
import com.rumble.domain.timerange.model.datasource.local.RoomWatchProgress
import com.rumble.network.dto.timerange.TimeRangeDataRequest
import com.rumble.network.dto.timerange.TimeRangeDto
import com.rumble.network.dto.timerange.TimeRangeEntryList
import com.rumble.videoplayer.player.TimeRangeData
import com.rumble.videoplayer.presentation.UiType

fun TimeRangeData.getWatchedTimeProgress() =
    RoomWatchProgress(
        videoId = videoId,
        startTime = startTime,
        duration = duration,
        isPlaceholder = isPlaceholder,
        playbackRate = playbackRate,
        deviceVolume = playbackVolume,
        muted = muted,
        uiType = uiType?.name,
    )

fun RoomWatchProgress.getTimeRange() =
    TimeRangeData(
        videoId = videoId,
        startTime = startTime,
        duration = duration,
        isPlaceholder = isPlaceholder == true,
        playbackRate = playbackRate,
        playbackVolume = deviceVolume ?: 100,
        muted = muted == true,
        uiType = uiType?.let { UiType.valueOf(it) }
    )

fun TimeRangeData.getRoomTimeRange() =
    RoomTimeRange(
        videoId = videoId,
        startTime = startTime,
        duration = duration
    )

fun RoomTimeRange.getTimeRange() =
    TimeRangeData(
        videoId = videoId,
        startTime = startTime,
        duration = duration
    )

fun TimeRangeData.getTimeRangeDto() =
    TimeRangeDto(
        videoId = videoId,
        startTime = startTime,
        duration = duration,
        isPlaceHolder = isPlaceholder
    )

fun List<TimeRangeData>.getTimeRangeRequest() =
    TimeRangeDataRequest(
        data = TimeRangeEntryList(
            entryList = this.map { it.getTimeRangeDto() }
        )
    )