package com.rumble.network.api

import com.rumble.network.dto.channel.ReportRequest
import com.rumble.network.dto.channel.ReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ReportApi {
    /**
     * Reports inappropriate content.
     */
    @Headers("Content-Type: application/json")
    @POST("service.php?name=report")
    suspend fun report(@Body data: ReportRequest): Response<ReportResponse>
}