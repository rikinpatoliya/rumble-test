package com.rumble.network.api

import com.rumble.network.dto.livevideo.LiveReportBody
import com.rumble.network.dto.livevideo.LiveReportResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface LiveVideoApi {

    @POST
    suspend fun reportLiveVideo(@Url url: String, @Body body: LiveReportBody, @Query("name") serviceName: String): Response<LiveReportResponse>
}