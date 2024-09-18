package com.rumble.domain.rumbleads.model.datasource

import com.rumble.network.api.PreRollApi
import com.rumble.network.api.RumbleAdsApi
import com.rumble.network.api.RumbleBannerApi
import com.rumble.network.dto.ads.rumble.AdListResponse
import com.rumble.network.dto.ads.rumble.RumbleAdResponse
import com.rumble.network.queryHelpers.PublisherId
import okhttp3.ResponseBody
import retrofit2.Response

interface RumbleAdsRemoteDataSource {
    suspend fun fetchRumbleAdList(
        limit: Int,
        keywords: String? = null,
        categories: String? = null,
        videoId: Long? = null,
        channelId: Long? = null,
        userId: Long? = null,
        currentUserId: Long?,
        genderId: Int?,
        ageBracketId: Int?
    ): Response<RumbleAdResponse>

    suspend fun reportAdImpression(impressionUrl: String)

    suspend fun fetchAdList(
        lastWatchedTime: Long,
        videoId: String,
        publisherId: PublisherId = PublisherId.AndroidApp,
        ignoreParams: Int?,
        adsDebug: Int?,
        enableMidrolls: Int,
    ): Response<AdListResponse>

    suspend fun sendAdEvent(url: String, time: Long): Response<ResponseBody>
}

class RumbleAdsRemoteDataSourceImpl(
    private val rumbleBannerApi: RumbleBannerApi,
    private val rumbleAdsApi: RumbleAdsApi,
    private val preRollApi: PreRollApi
) : RumbleAdsRemoteDataSource {

    override suspend fun fetchRumbleAdList(
        limit: Int,
        keywords: String?,
        categories: String?,
        videoId: Long?,
        channelId: Long?,
        userId: Long?,
        currentUserId: Long?,
        genderId: Int?,
        ageBracketId: Int?
    ): Response<RumbleAdResponse> =
        rumbleBannerApi.fetchAdList(
            limit = limit,
            keywords = keywords,
            videoId = videoId,
            channelId = channelId,
            userId = userId,
            categories = categories,
            currentUserId = currentUserId,
            gender = genderId,
            ageBracket = ageBracketId
        )

    override suspend fun reportAdImpression(impressionUrl: String) =
        rumbleAdsApi.reportImpression(impressionUrl)

    override suspend fun fetchAdList(
        lastWatchedTime: Long,
        videoId: String,
        publisherId: PublisherId,
        ignoreParams: Int?,
        adsDebug: Int?,
        enableMidrolls: Int,
    ): Response<AdListResponse> =
        preRollApi.fetchAdList(
            lastWatchedTime = lastWatchedTime,
            videoId = videoId,
            publisherId = publisherId,
            ignoreParams = ignoreParams,
            adsDebug = adsDebug,
            enableMidrolls = enableMidrolls
        )

    override suspend fun sendAdEvent(url: String, time: Long): Response<ResponseBody> =
        rumbleAdsApi.sendAdEvent(url = url, time = time)
}