package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FreshChannelListResult
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.channel.FreshChannel
import com.rumble.domain.feed.model.repository.FeedRepository
import java.time.ZoneOffset
import javax.inject.Inject

data class GetFreshChannelsUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(): FreshChannelListResult {
        return when (val result = feedRepository.fetchFreshChannelList()) {
            is ChannelListResult.Failure -> {
                rumbleErrorUseCase.invoke(rumbleError = result.rumbleError)
                FreshChannelListResult.Failure(rumbleError = result.rumbleError)
            }
            is ChannelListResult.Success -> FreshChannelListResult.Success(result.channelList.map {
                FreshChannel(
                    it,
                    isFresh(it)
                )
            })
        }
    }

    private suspend fun isFresh(channel: ChannelDetailsEntity): Boolean {
        val uploadTimestamp = channel.latestVideo?.uploadDate?.toEpochSecond(ZoneOffset.UTC) ?: 0
        val view = feedRepository.fetchChannelView(channel.channelId)
        return (view == null || view.time < uploadTimestamp)
    }

}