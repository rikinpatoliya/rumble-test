package com.rumble.domain.repost

import com.rumble.domain.feed.model.getPlayListUserEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.domain.repost.domain.domainmodel.ChannelEntity
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.network.dto.creator.Channel
import com.rumble.network.dto.repost.Repost
import com.rumble.utils.extension.convertUtcToLocal
import com.rumble.utils.extension.toUserIdString

fun Repost.getRepostEntity() =
    RepostEntity(
        id = id.toLong(),
        message = message,
        video = video.getVideoEntity(),
        user = user.getPlayListUserEntity(),
        channel = channel?.getChannelEntity(),
        creationDate = createdOn.convertUtcToLocal(),
    )

fun Channel.getChannelEntity() =
    ChannelEntity(
        id = id.toUserIdString(),
        url = url,
        title = title,
        name = name,
        picture = picture,
        followers = followers,
        verifiedBadge = verifiedBadge,
        followed = followed,
    )