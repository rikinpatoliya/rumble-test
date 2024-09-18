package com.rumble.domain.timerange.model.datasource.remote

import com.rumble.network.dto.timerange.TimeRangeDataRequest
import okhttp3.ResponseBody
import retrofit2.Response

interface TimeRangeRemoteDataSource {
    suspend fun sendTimeRangeList(
        timeRangeDataRequest: TimeRangeDataRequest,
        endpoint: String
    ): Response<ResponseBody>
}