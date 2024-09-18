package com.rumble.domain.timerange.model.datasource.remote

import com.rumble.network.api.VideoApi
import com.rumble.network.dto.timerange.TimeRangeDataRequest
import okhttp3.ResponseBody
import retrofit2.Response

class TimeRangeRemoteDataSourceImpl(
    private val videoApi: VideoApi
) : TimeRangeRemoteDataSource {
    override suspend fun sendTimeRangeList(
        timeRangeDataRequest: TimeRangeDataRequest,
        endpoint: String
    ): Response<ResponseBody> =
        videoApi.reportTimeRange(endpoint, timeRangeDataRequest)
}