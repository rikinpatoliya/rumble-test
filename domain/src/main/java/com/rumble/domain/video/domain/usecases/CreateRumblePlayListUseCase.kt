package com.rumble.domain.video.domain.usecases

import com.rumble.domain.analytics.domain.domainmodel.videoDetailsScreen
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.videoplayer.player.VideoStartMethod
import com.rumble.videoplayer.player.config.VideoScope
import com.rumble.videoplayer.player.internal.notification.RumblePlayList
import javax.inject.Inject

class CreateRumblePlayListUseCase @Inject constructor(
    private val createRumbleVideoUseCase: CreateRumbleVideoUseCase
) {

    suspend operator fun invoke(
        title: String = "",
        feedList: List<Feed>,
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
                screenId = videoDetailsScreen,
                includeMetadata = it.includeMetadata,
                requestLiveGateData = requestLiveGateData,
                videoScope = VideoScope.Other,
            )
        },
        shuffle = shuffle,
        loopPlayList = loop
    )
}

