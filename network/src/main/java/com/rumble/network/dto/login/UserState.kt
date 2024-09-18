package com.rumble.network.dto.login

import com.google.gson.annotations.SerializedName

/**
 * Gets returned by all requests to the /service.php under the user key
 *
 * @param userId Optional. null if "logged_in" is false
 * @param isAdmin Optional.
 * @param debug Optional.
 *
 */
data class UserState(
    @SerializedName("id")
    val userId: String?,
    @SerializedName("logged_in")
    val loggedIn: Boolean,
    @SerializedName("admin")
    val isAdmin: Boolean?,
    @SerializedName("debug")
    val debug: Debug?,
    @SerializedName("watching_now")
    val watchingNow: WatchingNow?,
    @SerializedName("chat")
    val chat: Chat?,
    @SerializedName("watch_time")
    val watchTime: WatchTime,
    @SerializedName("watch_progress_interval")
    val watchProgressInterval: Long,
    @SerializedName("e")
    val eventUrl: EventUrl,
)

/**
 *
 * @param canUseCustomApiDomain Optional.
 *
 */
data class Debug(
    @SerializedName("can_use_custom_api_domain")
    val canUseCustomApiDomain: Boolean?,
    @SerializedName("can_submit_logs")
    val canSubmitLogs: Boolean?,
)

data class WatchingNow(
    @SerializedName("subdomain")
    val subdomain: String,
    @SerializedName("ttl_post")
    val pingInterval: Int,
    @SerializedName("endpoint")
    val endpoint: String
)

data class Chat(
    @SerializedName("endpoint")
    val endpoint: String
)

data class WatchTime(
    @SerializedName("endpoint")
    val endpoint: String,
    @SerializedName("interval")
    val interval: Int
)

data class EventUrl(
    @SerializedName("url")
    val url: String
)
