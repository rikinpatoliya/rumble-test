package com.rumble.domain.discover.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelListResult
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.discover.model.repository.DiscoverRepository
import com.rumble.utils.RumbleConstants.LIMIT_TO_FIVE
import javax.inject.Inject
import kotlin.math.min

class GetTopChannelsUseCase @Inject constructor(
    private val discoverRepository: DiscoverRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke() =
        when (val result =
            discoverRepository.getFeaturedChannels(offset = 0, limit = LIMIT_TO_FIVE)) {
            is ChannelListResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                result
            }
            is ChannelListResult.Success -> {
                result.copy(
                    channelList = result.channelList.subList(
                        0,
                        min(
                            result.channelList.size,
                            LIMIT_TO_FIVE
                        )
                    )
                )
            }
        }
}

