package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelsResult
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import javax.inject.Inject

class GetUserUploadChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {

    suspend operator fun invoke(): UserUploadChannelsResult {
        val result = channelRepository.fetchUserUploadChannels()
        if (result is UserUploadChannelsResult.UserUploadChannelsError)
            rumbleErrorUseCase(result.rumbleError)
        return result
    }
}