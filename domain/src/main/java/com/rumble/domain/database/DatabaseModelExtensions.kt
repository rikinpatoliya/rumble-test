package com.rumble.domain.database

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelFollow
import com.rumble.domain.channels.channeldetails.domain.domainmodel.RoomChannelFollow
import com.rumble.domain.feed.domain.domainmodel.category.VideoCollectionView
import com.rumble.domain.feed.domain.domainmodel.category.VideoCollectionViewCount
import com.rumble.domain.feed.model.datasource.local.RoomVideoCollectionView
import com.rumble.domain.feed.model.datasource.local.RoomVideoCollectionViewCount

fun VideoCollectionView.getRoomVideoCollectionView(): RoomVideoCollectionView =
    RoomVideoCollectionView(
        id = id,
        name = videoCollectionName,
        viewTimestamp = viewTimestamp,
        userId = userId
    )

fun RoomVideoCollectionViewCount.getVideoCollectionViewCount(): VideoCollectionViewCount =
    VideoCollectionViewCount(
        collectionId = name,
        count = count,
    )

fun RoomChannelFollow.getChannelFollow(): ChannelFollow =
    ChannelFollow(
        channelId = channelId,
        followed = followed
    )
