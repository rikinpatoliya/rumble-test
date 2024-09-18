package com.rumble.network.api

import com.rumble.network.dto.ads.rumble.AdListResponse
import com.rumble.network.queryHelpers.PublisherId
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PreRollApi {
    @GET("embedJS/u{pub}/")
    suspend fun fetchAdList(
        @Path("pub") publisherId: PublisherId = PublisherId.AndroidApp,
        @Query("api") api: Int = 5,
        @Query("ver") version: Int = 2,
        @Query("request") request: String = "video",
        @Query("ad_wt") lastWatchedTime: Long = 0,
        @Query("v") videoId: String,
        @Query("ignore_params") ignoreParams: Int? = null,
        @Query("ads_debug") adsDebug: Int? = null,
        @Query("enable_midrolls") enableMidrolls: Int,
    ): Response<AdListResponse>
}