package com.rumble.network.api

import com.rumble.network.dto.ads.rumble.RumbleAdResponse
import com.rumble.network.queryHelpers.AdRichMedia
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RumbleBannerApi {
    @GET("23/json")
    suspend fun fetchAdList(
        @Query("count") limit: Int,
        @Query("cpid") publisherId: Int = 6,
        @Query("html") includeRichMedia: AdRichMedia = AdRichMedia.NOT_INCLUDE,
        @Query("keywords") keywords: String? = null,
        @Query("categories") categories: String? = null,
        @Query("cuid") userId: Long? = null,
        @Query("cvid") videoId: Long? = null,
        @Query("ccid") channelId: Long? = null,
        @Query("cuuid") currentUserId: Long? = null,
        @Query("cug") gender: Int? = null,
        @Query("cuab") ageBracket: Int?
    ): Response<RumbleAdResponse>
}