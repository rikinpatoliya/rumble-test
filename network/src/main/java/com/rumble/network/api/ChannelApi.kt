package com.rumble.network.api

import com.rumble.network.dto.channel.*
import okhttp3.FormBody
import retrofit2.Response
import retrofit2.http.*

interface ChannelApi {

    /**
     * Return a channel’s metadata
     *
     * @param channelId String, channel id
     */
    @GET("service.php?name=video_collection.meta")
    suspend fun fetchChannelData(
        @Query("id") channelId: String
    ): Response<ChannelResponse>

    /**
     * Subscribe, unsubscribe, block, or unblock a channel, change email notifications preferences.
     *
     * The channel can be in one of three states: followed, not followed, or blocked. The user can't follow a blocked channel.
     *
     * To change notifications settings, send notification and/or frequency parameter
     * action parameter is mandatory even for changing notifications settings, send the appropriate
     * value so that the subscription state wouldn't change.
     *
     * Always check the following/blocking and notifications settings state of the channel in
     * response and update the UI accordingly.
     *
     * All params should be part of the body for this POST request
     * @param channelId Required. Id of the video collection
     * @param type Required. Type of the video collection: channel, user, media
     * @param action Required. Action to perform: subscribe, unsubscribe, block, unblock
     * @param enableNotification Optional. true to enable email notifications, false to disable them
     * @param notificationFrequency Optional. Email notifications frequency. 1 - daily, 2 - instant, 3 - weekly, 4 - monthly
     * @param enablePush Optional. Integer (0 or 1) to enable push notifications for livestreams
     */
    @POST("service.php?name=user.subscribe")
    suspend fun updateSubscription(
        @Body subscriptionBody: FormBody
    ): Response<ChannelResponse>

    /**
     * Returns a list of the channels the user is subscribed to.
     *
     * <p/>
     * Requests with pagination support accept the following query parameters:
     * E.g. offset=0&limit=20 will get you the first 20 items (0...19), offset=20&limit=20 will get
     * the next set of 20 items (20...39), and so on.
     *
     * A request may return fewer than limit items. Regardless of the number of items returned, the
     * offset of the next request should be incremented by limit. Next pages can be requested until
     * a request returns empty results.
     *
     * Never rely on default values of the parameters and send explicit values.
     * <p/>
     *
     * @param channelId id of a channel. If missing, the followed channels of the current user are returned
     * @param offset The index of the first item to be returned by the request
     * @param limit The maximum amount of the items returned by the request
     */
    @GET("service.php?name=video_collection.following")
    suspend fun listOfFollowedChannels(
        @Query("id") channelId: String? = null,
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ChannelsResponse>

    /**
     * Returns a list of featured channels. The request returns random list of channels each time
     * it's called and doesn't support pagination.
     */
    @GET("service.php?name=video_collection.featured")
    suspend fun fetchFeaturedChannels(
        @Query("offset") offset: Int? = null,
        @Query("limit") limit: Int? = null
    ): Response<ChannelsResponse>

    /**
     * Returns a list of channels with fresh content
     *
     */
    @GET("service.php?name=user.subscription_latest")
    suspend fun fetchFreshChannels(): Response<ChannelsResponse>

    @GET("service.php?name=user.following_list")
    suspend fun fetchFollowedChannels(): Response<FollowedChannelsResponse>
}