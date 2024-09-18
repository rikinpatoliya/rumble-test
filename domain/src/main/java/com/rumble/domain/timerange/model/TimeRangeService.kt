package com.rumble.domain.timerange.model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.events.domain.usecases.DeleteReportedWatchProgressUseCase
import com.rumble.domain.events.domain.usecases.GetWatchProgressListUseCase
import com.rumble.domain.events.domain.usecases.SendWatchProgressEventListUseCase
import com.rumble.domain.timerange.domain.usecases.DeleteTimeRangeListUseCase
import com.rumble.domain.timerange.domain.usecases.GetTimeRangeListUseCase
import com.rumble.domain.timerange.domain.usecases.SendTimeRangeUseCase
import com.rumble.network.NetworkRumbleConstants
import com.rumble.network.di.IoDispatcher
import com.rumble.network.session.SessionManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TAG = "TimeRangeService"

@AndroidEntryPoint
class TimeRangeService : Service() {
    @Inject
    lateinit var sessionManager: SessionManager
    @Inject
    lateinit var getTimeRangeListUseCase: GetTimeRangeListUseCase
    @Inject
    lateinit var getWatchProgressListUseCase: GetWatchProgressListUseCase
    @Inject
    lateinit var deleteTimeRangeListUseCase: DeleteTimeRangeListUseCase
    @Inject
    lateinit var deleteReportedWatchProgressUseCase: DeleteReportedWatchProgressUseCase
    @Inject
    lateinit var sendTimeRangeUseCase: SendTimeRangeUseCase
    @Inject
    lateinit var sendWatchProgressEventListUseCase: SendWatchProgressEventListUseCase
    @Inject
    lateinit var unhandledErrorUseCase: UnhandledErrorUseCase
    @Inject
    @IoDispatcher
    lateinit var ioDispatcher: CoroutineDispatcher


    private lateinit var scope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        scope = CoroutineScope(ioDispatcher)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            deleteTimeRangeListUseCase()
            while (isActive) {
                var interval: Long = sessionManager.timeRangeInterval.first().toLong()
                interval = if (interval == 0L) NetworkRumbleConstants.TIME_RANGE_UPLOAD_PERIOD
                else TimeUnit.SECONDS.toMillis(interval)
                delay(interval)
                sendTimeRange()
            }
        }
        scope.launch {
            deleteReportedWatchProgressUseCase()
            while (isActive) {
                val watchProgressInterval: Long = sessionManager.watchProgressIntervalFlow.first().toLong()
                val delayInterval: Long = if (watchProgressInterval == 0L) NetworkRumbleConstants.TIME_RANGE_UPLOAD_PERIOD
                else TimeUnit.SECONDS.toMillis(watchProgressInterval)
                delay(delayInterval)
                if (watchProgressInterval == 0L) {
                    deleteReportedWatchProgressUseCase()
                } else {
                    sendWatchProgressEvent()
                }
            }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        scope.launch { sendTimeRange() }
        stopSelf()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private suspend fun sendTimeRange() {
        try {
            val endpoint = sessionManager.timeRangeEndpoint.first()
            val timeRangeList = getTimeRangeListUseCase()
            deleteTimeRangeListUseCase()
            if (timeRangeList.isNotEmpty())
                sendTimeRangeUseCase(timeRangeList, endpoint)
        } catch (e: Throwable) {
            unhandledErrorUseCase(TAG, e)
        }
    }

    private suspend fun sendWatchProgressEvent() {
        try {
            val timeRangeList = getWatchProgressListUseCase()
            deleteReportedWatchProgressUseCase()
            if (timeRangeList.isNotEmpty()) {
                sendWatchProgressEventListUseCase(timeRangeList)
            }
        } catch (e: Throwable) {
            unhandledErrorUseCase(TAG, e)
        }
    }
}