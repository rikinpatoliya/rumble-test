package com.rumble.network.dto.creator

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.video.LocalsCommunity
import com.rumble.network.dto.video.Video

/**
 * Channel model
 *
 * @param id The channel id
 * @param type The channel type "channel", "user" or "media"
 * @param title The channel title
 * @param name The channel name
 * @param thumbnail Optional. URL. The channel thumbnail
 * @param backSplash Optional. URL. The channel back splash
 * @param videos Amount of uploaded videos
 * @param rumbles Amount of rumbles (some number depending on likes/dislikes)
 * @param followers Amount of followers
 * @param following Amount of followers
 * @param followed Whether the channel is followed by the user
 * @param blocked Optional, whether the channel is blocked by the user. Nill if "followed" == false
 * @param notification Optional, whether email notifications are enabled. Nill if "followed" == false
 * @param notificationFrequency Optional, email notifications frequiency. Nill if "followed" == false
 * @param isPushLiveStreamsEnabled Optional, enable push notifications for livestreams. Nill if "followed" == false
 * @param latestVideo Optional, the latest video of the channel
 * @param localsCommunity Optional, the locals community of the channel
 * @param url // Optional, url of the channel on rumble.com
 */
data class Creator(
    @SerializedName("id")
    val id: String,
    @SerializedName("type")
    val type: String?,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("thumb")
    val thumbnail: String?,
    @SerializedName("backsplash")
    val backSplash: String?,
    @SerializedName("videos")
    val videos: Int,
    @SerializedName("rumbles")
    val rumbles: Int,
    @SerializedName("followers")
    val followers: Int,
    @SerializedName("following")
    val following: Int,
    @SerializedName("followed")
    val followed: Boolean,
    @SerializedName("blocked")
    val blocked: Boolean?,
    @SerializedName("notification")
    val notification: Boolean?,
    @SerializedName("notification_frequency")
    val notificationFrequency: Int?,
    @SerializedName("is_push_ls_enabled")
    val isPushLiveStreamsEnabled: Boolean?,
    @SerializedName("latest_video")
    val latestVideo: Video?,
    @SerializedName("featured_video")
    val featuredVideo: Video?,
    @SerializedName("locals_community")
    val localsCommunity: LocalsCommunity?,
    @SerializedName("verified_badge")
    val verifiedBadge: Boolean?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("watching_now_count")
    val watchingNowCount: Int?
)
