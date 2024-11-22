package com.rumble.domain.repost.domain.domainmodel

import android.os.Parcelable
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.UserEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class RepostEntity(
    override val index: Int = 0,
    override val id: Long,
    val message: String,
    val video: VideoEntity,
    val user: UserEntity,
    val channel: ChannelEntity?,
    val creationDate: LocalDateTime
) : Feed, Parcelable
