package com.rumble.network.dto.video

import com.google.gson.annotations.SerializedName
import com.rumble.network.dto.livechat.LiveGate


/**
 * @param videoId same id as in input.
 * @param numWatchingNow current number of people watching this video now
 * @param livestreamStatus current status of the livestream, or null if the video is not a livestream (anymore)
 */
data class WatchingNowData(
    @SerializedName("video_id")
    val videoId: Int,
    @SerializedName("num_watching_now")
    val numWatchingNow: Int,
    @SerializedName("livestream_status")
    val livestreamStatus: Int,
    @SerializedName("live_gate")
    val liveGate: LiveGate?,
)
