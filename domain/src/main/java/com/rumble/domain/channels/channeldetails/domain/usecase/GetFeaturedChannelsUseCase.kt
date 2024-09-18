package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.model.repository.ChannelRepository
import javax.inject.Inject

class GetFeaturedChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {

    suspend operator fun invoke(): List<ChannelDetailsEntity> =
        when (val result = channelRepository.listOfFeaturedChannels()) {
            is ChannelListResult.Failure -> emptyList()
            is ChannelListResult.Success -> result.channelList
        }
}