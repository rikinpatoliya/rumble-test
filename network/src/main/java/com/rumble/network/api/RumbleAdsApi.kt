package com.rumble.network.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Url

interface RumbleAdsApi {

    @GET
    suspend fun reportImpression(@Url impressionUrl: String)

    @POST
    suspend fun sendAdEvent(@Url url: String, @Query("et") time: Long): Response<ResponseBody>
}