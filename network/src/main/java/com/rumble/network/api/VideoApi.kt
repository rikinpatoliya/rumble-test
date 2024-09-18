package com.rumble.network.api

import com.rumble.network.dto.collection.CollectionListResponse
import com.rumble.network.dto.collection.CollectionListWithoutVideosResponse
import com.rumble.network.dto.comments.CommentVoteBody
import com.rumble.network.dto.comments.CommentVoteResponse
import com.rumble.network.dto.comments.UserCommentResponse
import com.rumble.network.dto.livevideo.LiveReportBody
import com.rumble.network.dto.timerange.TimeRangeDataRequest
import com.rumble.network.dto.video.*
import com.rumble.network.queryHelpers.BattlesType
import com.rumble.network.queryHelpers.Options
import com.rumble.network.queryHelpers.PlayListInclude
import com.rumble.network.queryHelpers.Sort
import okhttp3.FormBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface VideoApi {

    @GET("service.php?name=user.subscription_feed")
    suspend fun fetchSubscriptionVideoList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("options") options: String = listOf(Options.WATCHING_PROGRESS).joinToString(separator = ",")
    ): Response<VideoListResponse>

    @GET("service.php?name=video_collection.videos")
    suspend fun fetchVideoCollection(
        @Query("id") id: String,
        @Query("sort") sortType: Sort? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("options") options: String = listOf(Options.FULL, Options.WATCHING_PROGRESS).joinToString(separator = ",")
    ): Response<VideoListResponse>

    /**
     * Returns a list of live videos
     *
     * @param front {0,1}   0 is full list
     *                      1 is short list (front page on web app)
     */
    @GET("service.php?name=video_collection.live&options=video.full")
    suspend fun fetchLiveVideoList(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("front") front: Int
    ): Response<VideoListResponse>

    @POST("service.php?name=video.vote")
    suspend fun likeVideo(@Body voteBody: VideoVoteBody): Response<VideoVoteResponse>

    @GET("service.php?name=media.details")
    suspend fun fetchVideoDetails(
        @Query("id") id: Long? = null,
        @Query("url") url: String? = null,
        @Query("options") options: String? = null
    ): Response<VideoDetailsResponse>

    @GET("service.php?name=video_collection.live")
    suspend fun fetchLiveVideos(
        @Query("front") front: Int = 0,
        @Query("options") options: String = listOf(Options.FULL, Options.WATCHING_PROGRESS).joinToString(separator = ","),
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
    ): Response<VideoListResponse>

    @GET("service.php?name=battle.videos")
    suspend fun fetchBattlesVideos(
        @Query("type") battlesType: BattlesType? = null,
        @Query("options") options: String = listOf(Options.WATCHING_PROGRESS).joinToString(separator = ","),
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
    ): Response<VideoListResponse>

    @GET("service.php?name=playlist.list_videos")
    suspend fun fetchPlayListVideos(
        @Query("playlist_id") playlistId: String,
        @Query("options") options: String = listOf(Options.FULL, Options.WATCHING_PROGRESS).joinToString(separator = ","),
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
    ): Response<PlayListVideoListResponse>

    @GET("service.php?name=user.purchases")
    suspend fun fetchPurchases(
        @Query("options") options: String = listOf(Options.FULL, Options.WATCHING_PROGRESS).joinToString(separator = ","),
    ): Response<VideoListResponse>

    @GET("service.php?name=playlist.get")
    suspend fun fetchPlayList(
        @Query("playlist_id") playListId: String,
        @Query("options") options: String = listOf(Options.FULL, Options.WATCHING_PROGRESS).joinToString(separator = ","),
    ): Response<PlayListResponse>

    @GET("service.php?name=playlist.list")
    suspend fun fetchPlayLists(
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null,
        @Query("include") include: PlayListInclude? = null,
        @Query("extra_has_videos_ids") extraHasVideosIds: List<Long>? = null,
    ): Response<PlayListsResponse>

    @FormUrlEncoded
    @POST("service.php?name=playlist.add_video")
    suspend fun addVideoToPlaylist(
        @Field("playlist_id") playlistId: String,
        @Field("video_id") videoId: Long,
    ): Response<AddVideoToPlaylistResponse>

    @FormUrlEncoded
    @POST("service.php?name=playlist.delete_video")
    suspend fun removeVideoToPlaylist(
        @Field("playlist_id") playlistId: String,
        @Field("video_id") videoId: Long,
    ): Response<RemoveFromPlaylistResponse>

    @FormUrlEncoded
    @POST("service.php?name=playlist.follow")
    suspend fun followPlayList(
        @Field("playlist_id") playlistId: String,
    ): Response<FollowPlayListResponse>

    @FormUrlEncoded
    @POST("service.php?name=playlist.delete")
    suspend fun deletePlayList(
        @Field("playlist_id") playlistId: String,
    ): Response<DeletePlayListResponse>

    @POST("service.php?name=watch_history.clear")
    suspend fun clearWatchHistory(): Response<ClearWatchHistoryResponse>

    @FormUrlEncoded
    @POST("service.php?name=playlist.unfollow")
    suspend fun unFollowPlayList(
        @Field("playlist_id") playlistId: String,
    ): Response<FollowPlayListResponse>

    @FormUrlEncoded
    @POST("service.php?name=playlist.add")
    suspend fun addPlayList(
        @Field("title") title: String,
        @Field("description") description: String?,
        @Field("visibility") visibility: String?,
        @Field("channel_id") channelId: Long?,
    ): Response<UpdatePlayListResponse>

    @FormUrlEncoded
    @POST("service.php?name=playlist.edit")
    suspend fun editPlayList(
        @Field("playlist_id") playlistId: String,
        @Field("title") title: String,
        @Field("description") description: String?,
        @Field("visibility") visibility: String?,
        @Field("channel_id") channelId: Long?,
    ): Response<UpdatePlayListResponse>

    /**
     * The application should be "pinging" server with this request approximately every 45 seconds
     * as long as the user has a live stream video page open, regardless of whether the stream is
     * being played or stopped/paused/finished.
     *
     * It's up to the backend to decide whether it wants to count viewers of a particular video or
     * not, so it's normal for the returned value of num_watching_now to be zero in some cases.
     *
     * It is not considered an error when video_id refers to a video that is not a live stream,
     * the server will just return num_watching_now: 0 and livestream_status: null. However,
     * an application should not be adversely sending this request when it knows that the video
     * is not a live stream.
     */
    @POST("service.php?name=video.watching-now")
    suspend fun reportWatchingLiveStream(
        @Body body: LiveReportBody
    ): Response<WatchingNowResponse>

    /**
     * Log a view of a video page
     * The path to send the request to is taken from the [RumbleLog] attribute of a [Video] object.
     */
    @GET("{view}")
    suspend fun reportVideoPageView(
        @Path(
            value = "view",
            encoded = true
        ) view: String
    ): Response<ResponseBody>

    @POST("service.php?name=comment.add")
    suspend fun postComment(@Body body: FormBody): Response<UserCommentResponse>

    @POST("service.php?name=comment.delete")
    suspend fun deleteComment(@Query("comment_id") commentId: Long): Response<UserCommentResponse>

    @POST("service.php?name=comment.vote")
    suspend fun voteComment(@Body voteBody: CommentVoteBody): Response<CommentVoteResponse>

    @GET("service.php?name=video_collection.pills&with_videos=1")
    suspend fun fetchCollectionList(
        @Query("options") options: String = listOf(
            Options.FULL,
            Options.WATCHING_PROGRESS
        ).joinToString(separator = ","),
    ): Response<CollectionListResponse>

    @GET("service.php?name=video_collection.pills")
    suspend fun fetchCollectionListWithoutVideos(): Response<CollectionListWithoutVideosResponse>

    @GET
    suspend fun fetchPlaylist(@Url url: String): Response<ResponseBody>

    @POST
    suspend fun reportTimeRange(
        @Url url: String,
        @Body timeRangeDataRequest: TimeRangeDataRequest,
    ): Response<ResponseBody>

    @GET("service.php?name=video.autoplay")
    suspend fun fetchRelatedVideoList(@Query("video_id") videoId: Long): Response<RelatedVideoResponse>
}