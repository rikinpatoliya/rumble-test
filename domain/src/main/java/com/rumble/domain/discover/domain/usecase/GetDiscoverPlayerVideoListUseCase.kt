package com.rumble.domain.discover.domain.usecase

import androidx.paging.PagingData
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FreshChannelListResult
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.discover.domain.domainmodel.DiscoverPlayerVideoResult
import com.rumble.domain.discover.model.DiscoverPlayerVideoListSource
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.usecase.GetFreshChannelsUseCase
import com.rumble.domain.videolist.domain.model.VideoList
import com.rumble.domain.videolist.domain.usecase.GetVideoListUseCase
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetDiscoverPlayerVideoListUseCase @Inject constructor(
    private val getFreshChannelsUseCase: GetFreshChannelsUseCase,
    private val getVideoListUseCase: GetVideoListUseCase,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke(
        source: DiscoverPlayerVideoListSource,
        channelId: String
    ): DiscoverPlayerVideoResult {
        return when (source) {
            DiscoverPlayerVideoListSource.Battles -> {
                DiscoverPlayerVideoResult(getVideoListUseCase(VideoList.Battles))
            }

            DiscoverPlayerVideoListSource.FreshContent -> {
                val freshChannels = getFreshChannelsUseCase()
                getLatestVideoListPagingDataFlow(freshChannels, channelId)
            }
        }
    }

    private fun getLatestVideoListPagingDataFlow(
        freshChannels: FreshChannelListResult,
        channelId: String,
    ): DiscoverPlayerVideoResult {
        val videoList = mutableListOf<Feed>()
        var videoIndex = 0
        var scrollToVideoEntity: VideoEntity? = null
        (freshChannels as? FreshChannelListResult.Success)?.channels?.forEach { channel ->
            channel.channelDetailsEntity.latestVideo?.let { videoEntity ->
                videoList.add(videoEntity)
                if (channel.channelDetailsEntity.channelId == channelId) {
                    videoIndex = videoList.indexOf(videoEntity)
                    scrollToVideoEntity = videoEntity
                }
            }
        }
        return DiscoverPlayerVideoResult(flowOf(PagingData.from(videoList)), videoIndex, scrollToVideoEntity)
    }
}