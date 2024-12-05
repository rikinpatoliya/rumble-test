package com.rumble.network.api

import com.rumble.network.dto.repost.DeleteRepostResponse
import com.rumble.network.dto.repost.RepostListResponse
import com.rumble.network.dto.repost.RepostResponse
import com.rumble.network.queryHelpers.Options
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RepostApi {
    @GET("service.php?name=video_repost.feed")
    suspend fun fetchFeedReposts(
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("options") options: String = Options.FULL.value
    ): Response<RepostListResponse>

    @GET("service.php?name=video_repost.list")
    suspend fun fetchReposts(
        @Query("user_id") userId: String? = null,
        @Query("channel_id") channelId: String? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("options") options: String = Options.FULL.value
    ): Response<RepostListResponse>

    @DELETE("service.php?name=video_repost.delete")
    suspend fun deleteRepost(
        @Query("id") repostId: Long
    ): Response<DeleteRepostResponse>

    @FormUrlEncoded
    @POST("service.php?name=video_repost.add")
    suspend fun addRepost(
        @Field("video_id") videoId: Long,
        @Field("channel_id") channelId: Long?,
        @Field("message") message: String,
        @Query("options") options: String = Options.FULL.value,
    ): Response<RepostResponse>
}