package com.rumble.domain.feed.domain.domainmodel.video

import android.os.Parcelable
import com.rumble.domain.common.domain.domainmodel.UniqueId
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.category.VideoCategoryEntity
import com.rumble.domain.feed.domain.domainmodel.comments.CommentEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveGateEntity
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.network.dto.LiveStreamStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class VideoEntity(
    override val id: Long,
    val description: String?,
    val videoThumbnail: String,
    val numberOfView: Int,
    val url: String,
    val channelThumbnail: String,
    val channelId: String,
    val channelName: String,
    val videoStatus: VideoStatus,
    val uploadDate: LocalDateTime,
    val scheduledDate: LocalDateTime?,
    val watchingNow: Long,
    val duration: Long,
    val title: String,
    val commentNumber: Long,
    val viewsNumber: Long,
    var likeNumber: Long,
    var dislikeNumber: Long,
    var userVote: UserVote,
    val videoSourceList: List<VideoSource>,
    val channelFollowers: Int,
    var channelFollowed: Boolean,
    val channelBlocked: Boolean,
    val portraitMode: Boolean,
    val videoWidth: Int,
    val videoHeight: Int,
    val livestreamStatus: LiveStreamStatus,
    val liveDateTime: LocalDateTime?,
    val liveStreamedOn: LocalDateTime?,
    val supportsDvr: Boolean,
    val videoLogView: VideoLogView,
    val commentList: List<CommentEntity>?,
    val commentsDisabled: Boolean,
    val relatedVideoList: List<VideoEntity>?,
    val tagList: List<String>?,
    val categoriesList: List<VideoCategoryEntity>?,
    val verifiedBadge: Boolean,
    val ppv: PpvEntity? = null,
    val ageRestricted: Boolean,
    override val index: Int = 0,
    val liveChatDisabled: Boolean,
    override val uuid: UUID = UUID.randomUUID(),
    val lastPositionSeconds: Long?,
    val includeMetadata: Boolean,
    val isPremiumExclusiveContent: Boolean,
    val subscribedToCurrentChannel: Boolean,
    val hasLiveGate: Boolean,
    val liveGateEntity: LiveGateEntity? = null,
    val repostCount: Int,
    val userRepostList: List<RepostEntity>,
) : Feed, UniqueId, Parcelable
