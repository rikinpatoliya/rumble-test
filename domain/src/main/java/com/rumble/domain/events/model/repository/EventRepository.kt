package com.rumble.domain.events.model.repository

import com.rumble.domain.events.domain.domainmodel.SendWatchProgressEventsResult
import com.rumble.videoplayer.player.TimeRangeData

interface EventRepository {

    fun saveWatchProgress(timeRangeData: TimeRangeData)
    suspend fun sendWatchProgressEvents(
        eventEndpoint: String,
        timeRangeList: List<TimeRangeData>,
        userId: Long?,
        userIdString: String,
        subdomain: String
    ): SendWatchProgressEventsResult

    suspend fun clearWatchProgress()
    suspend fun getTimeRangeList(): List<TimeRangeData>
    suspend fun sendAnalyticsEvent(
        userId: String,
        eventName: String,
        eventParams: Map<String, String?>
    )
}