package com.rumble.domain.feed.domain.domainmodel.video

import android.os.Parcelable
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.common.domain.domainmodel.UniqueId
import com.rumble.domain.library.domain.model.PlayListVisibility
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime
import java.util.UUID

@Parcelize
data class PlayListEntity(
    val id: String = "",
    var title: String = "",
    var description: String = "",
    var visibility: PlayListVisibility = PlayListVisibility.PUBLIC,
    val isFollowing: Boolean = false,
    val url: String = "",
    val thumbnail: String = "",
    val updatedDate: LocalDateTime = LocalDateTime.now(),
    var playListOwnerId: String = "",
    val playListUserEntity: UserEntity = UserEntity(),
    val playListChannelEntity: PlayListChannelEntity? = null,
    val videosQuantity: Int = 0,
    var username: String = "",
    var channelName: String? = null,
    val channelThumbnail: String = "",
    val channelId: String = "",
    val verifiedBadge: Boolean = false,
    val followers: Int = 0,
    val followStatus: FollowStatus? = null,
    val videoIds: MutableList<Long>? = null,
    val videos: MutableList<PlaylistVideoEntity> = mutableListOf(),
    override val uuid: UUID = UUID.randomUUID(),
) : Parcelable, UniqueId