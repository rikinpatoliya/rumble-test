package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import javax.inject.Inject


class FetchFollowedChannelsUseV2Case @Inject constructor(
    private val channelRepository: ChannelRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke(): ChannelListResult {
        val result = channelRepository.fetchFollowedChannelsV2()
        if (result is ChannelListResult.Failure)
            rumbleErrorUseCase(result.rumbleError)
        return result

    }
}