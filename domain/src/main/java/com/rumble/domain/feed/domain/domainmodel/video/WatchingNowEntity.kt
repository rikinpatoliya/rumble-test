package com.rumble.domain.feed.domain.domainmodel.video

import com.rumble.network.dto.LiveStreamStatus

/**
 * @param videoId same id as in input.
 * @param numWatchingNow current number of people watching this video now
 * @param livestreamStatus current status of the livestream, or null if the video is not a livestream (anymore)
 */
data class WatchingNowEntity(
    val videoId: Int,
    val numWatchingNow: Int,
    val livestreamStatus: LiveStreamStatus,
)