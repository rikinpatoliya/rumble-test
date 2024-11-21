package com.rumble.domain.repost

import com.rumble.domain.feed.model.getChannelDetailsEntity
import com.rumble.domain.feed.model.getPlayListUserEntity
import com.rumble.domain.feed.model.getVideoEntity
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.network.dto.repost.Repost
import com.rumble.utils.extension.convertUtcToLocal

fun Repost.getRepostEntity() =
    RepostEntity(
        id = id,
        message = message,
        video = video.getVideoEntity(),
        user = user.getPlayListUserEntity(),
        channel = channel?.getChannelDetailsEntity(),
        creationDate = createdOn.convertUtcToLocal(),
    )