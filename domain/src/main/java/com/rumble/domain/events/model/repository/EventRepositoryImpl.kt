package com.rumble.domain.events.model.repository

import com.rumble.domain.events.domain.domainmodel.EventName
import com.rumble.domain.events.domain.domainmodel.SendWatchProgressEventsResult
import com.rumble.domain.events.model.datasource.EventRemoteDataSource
import com.rumble.domain.timerange.model.datasource.local.WatchProgressDao
import com.rumble.domain.timerange.model.getTimeRange
import com.rumble.domain.timerange.model.getWatchedTimeProgress
import com.rumble.network.dto.events.EventBody
import com.rumble.network.dto.events.EventDto
import com.rumble.videoplayer.player.TimeRangeData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.concurrent.TimeUnit

class EventRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val eventRemoteDataSource: EventRemoteDataSource,
    private val appRequestName: String,
    private val appVersion: String,
    private val osVersion: String,
    private val watchProgressDao: WatchProgressDao
) : EventRepository {

    private val scope = CoroutineScope(dispatcher)

    override fun saveWatchProgress(timeRangeData: TimeRangeData) {
        scope.launch {
            watchProgressDao.saveWatchProgress(timeRangeData.getWatchedTimeProgress())
        }
    }

    override suspend fun sendWatchProgressEvents(
        eventEndpoint: String,
        timeRangeList: List<TimeRangeData>,
        userId: Long?,
        userIdString: String,
        subdomain: String
    ): SendWatchProgressEventsResult = withContext(dispatcher) {
        val body = EventBody(
            appName = appRequestName,
            appVersion = appVersion,
            osInfo = osVersion,
            eventList = timeRangeList.map {
                EventDto(
                    eventName = EventName.WATCH_PROGRESS.value,
                    clientTime = TimeUnit.SECONDS.toMillis(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)),
                    userId = userId,
                    event = it.getWatchProgressEvent()
                )
            }
        )
        val response = eventRemoteDataSource.sendWatchProgressEvents(
            eventEndpoint = eventEndpoint,
            eventBody = body
        )
        if (response.isSuccessful and (response.body() != null)) {
            sendDebugEvent(body, subdomain, userIdString)
            SendWatchProgressEventsResult.Success(
                eventEndpoint = response.body()?.eventUrl?.url ?: ""
            )
        } else {
            SendWatchProgressEventsResult.Failure
        }
    }

    override suspend fun clearWatchProgress() = withContext(dispatcher) {
        watchProgressDao.bulkDelete()
    }

    override suspend fun getTimeRangeList(): List<TimeRangeData> = withContext(dispatcher) {
        watchProgressDao.getAllWatchProgress().map { it.getTimeRange() }
    }

    override suspend fun sendAnalyticsEvent(
        userId: String,
        eventName: String,
        eventParams: Map<String, String>
    ) {
        withContext(dispatcher) {
            eventRemoteDataSource.sendDebugAnalyticsEvent(userId, eventName, eventParams)
        }
    }

    private suspend fun sendDebugEvent(eventBody: EventBody, subdomain: String, userId: String) {
        if (subdomain.isNotEmpty()) {
            eventRemoteDataSource.sendAnalyticsEvent(userId, eventBody)
        }
    }
}