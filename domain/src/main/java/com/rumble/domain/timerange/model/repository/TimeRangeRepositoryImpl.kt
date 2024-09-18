package com.rumble.domain.timerange.model.repository

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.timerange.domain.domainmodel.SendTimeRangeListResult
import com.rumble.domain.timerange.model.datasource.local.TimeRangeDao
import com.rumble.domain.timerange.model.datasource.remote.TimeRangeRemoteDataSource
import com.rumble.domain.timerange.model.getRoomTimeRange
import com.rumble.domain.timerange.model.getTimeRange
import com.rumble.domain.timerange.model.getTimeRangeRequest
import com.rumble.network.NetworkRumbleConstants.TIME_RANGE_REPORT_PATH
import com.rumble.videoplayer.player.TimeRangeData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "TimeRangeRepositoryImpl"

class TimeRangeRepositoryImpl(
    private val dispatcher: CoroutineDispatcher,
    private val timeRangeDao: TimeRangeDao,
    private val timeRangeRemoteDataSource: TimeRangeRemoteDataSource
) : TimeRangeRepository {

    private val scope = CoroutineScope(dispatcher)

    override fun saveTimeRange(timeRangeData: TimeRangeData) {
        scope.launch {
            timeRangeDao.saveTimeRange(timeRangeData.getRoomTimeRange())
        }
    }

    override suspend fun getTimeRangeList(): List<TimeRangeData> = withContext(dispatcher) {
        timeRangeDao.getAllTimeRanges().map { it.getTimeRange() }
    }

    override suspend fun clearReportedTimeRanges() = withContext(dispatcher) {
        timeRangeDao.bulkDelete()
    }

    override suspend fun sendTimeRangeList(
        timeRangeData: List<TimeRangeData>,
        endpoint: String
    ): SendTimeRangeListResult = withContext(dispatcher) {
        val response = timeRangeRemoteDataSource.sendTimeRangeList(
            timeRangeData.getTimeRangeRequest(),
            "$endpoint/$TIME_RANGE_REPORT_PATH"
        )
        if (response.isSuccessful) SendTimeRangeListResult.Success
        else SendTimeRangeListResult.Failure(RumbleError(TAG, response.raw()))
    }
}