package com.rumble.domain.timerange.model.repository

import com.rumble.domain.timerange.domain.domainmodel.SendTimeRangeListResult
import com.rumble.videoplayer.player.TimeRangeData

interface TimeRangeRepository {
    fun saveTimeRange(timeRangeData: TimeRangeData)
    suspend fun getTimeRangeList(): List<TimeRangeData>
    suspend fun clearReportedTimeRanges()
    suspend fun sendTimeRangeList(timeRangeData: List<TimeRangeData>, endpoint: String): SendTimeRangeListResult
}