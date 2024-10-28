package com.rumble.domain.video.domain.usecases

import com.rumble.domain.analytics.domain.domainmodel.videoDetailsScreen
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.network.queryHelpers.PublisherId
import com.rumble.videoplayer.player.VideoStartMethod
import com.rumble.videoplayer.player.internal.notification.RumblePlayList
import javax.inject.Inject

class CreateRumblePlayListUseCase @Inject constructor(
    private val createRumbleVideoUseCase: CreateRumbleVideoUseCase
) {

    suspend operator fun invoke(
        title: String = "",
        feedList: List<Feed>,
        publisherId: PublisherId,
        shuffle: Boolean,
        loop: Boolean,
        requestLiveGateData: Boolean = false,
        applyLastPosition: Boolean = true
    ) = RumblePlayList(
        title = title,
        videoList = feedList.filterIsInstance<VideoEntity>().map {
            createRumbleVideoUseCase(
                videoEntity = it,
                restrictBackground = false,
                loopWhenFinished = false,
                applyLastPosition = applyLastPosition,
                videoStartMethod = VideoStartMethod.URL_PROVIDED,
                useLowQuality = false,
                relatedVideoList = emptyList(),
                publisherId = publisherId,
                screenId = videoDetailsScreen,
                includeMetadata = it.includeMetadata,
                requestLiveGateData = requestLiveGateData,
            )
        },
        shuffle = shuffle,
        loopPlayList = loop
    )
}

