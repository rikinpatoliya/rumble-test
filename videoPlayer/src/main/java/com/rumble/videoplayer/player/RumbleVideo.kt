package com.rumble.videoplayer.player

import com.rumble.network.queryHelpers.PublisherId
import com.rumble.videoplayer.player.config.BackgroundMode
import com.rumble.videoplayer.player.config.PlayerVideoSource
import com.rumble.videoplayer.player.config.RumbleVideoStatus
import com.rumble.videoplayer.player.config.StreamStatus
import java.time.LocalDateTime

data class RumbleVideo(
    val videoId: Long = 0,
    val videoList: List<PlayerVideoSource> = emptyList(),
    val videoThumbnailUri: String? = null,
    val supportsDvr: Boolean = false,
    val watchingNow: Long = 0,
    val scheduledDate: LocalDateTime? = null,
    val backgroundMode: BackgroundMode = BackgroundMode.On,
    val loop: Boolean = false,
    val title: String = "",
    val description: String = "",
    val streamStatus: StreamStatus = StreamStatus.NotStream,
    val lastPosition: Long = 0,
    val method: VideoStartMethod = VideoStartMethod.URL_PROVIDED,
    val useLowQuality: Boolean = false,
    val ageRestricted: Boolean = false,
    val channelName: String = "",
    val channelIcon: String = "",
    val channelId: String = "",
    val channelFollowers: Int = 0,
    val displayVerifiedBadge: Boolean = false,
    val relatedVideoList: List<RumbleVideo> = emptyList(),
    val duration: Long = 0,
    val publisherId: PublisherId = PublisherId.AndroidApp,
    val screenId: String = "",
    val verifiedBadge: Boolean = false,
    val uploadDate: LocalDateTime,
    val viewsNumber: Long = 0,
    val videoStatus: RumbleVideoStatus = RumbleVideoStatus.UPLOADED,
    val livestreamStatus: RumbleLiveStreamStatus = RumbleLiveStreamStatus.UNKNOWN,
    val liveDateTime: LocalDateTime? = null,
    val liveStreamedOn: LocalDateTime? = null,
    val likeNumber: Long = 0,
    val dislikeNumber: Long = 0,
    val userId: String = "",
    val isPremiumExclusiveContent: Boolean = false,
    val requestLiveGateData: Boolean = false,
    val hasLiveGate: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RumbleVideo) return false
        return videoId == other.videoId
    }

    override fun hashCode(): Int {
        return 31 * videoId.hashCode()
    }
}
