package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FetchChannelDataResult
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import javax.inject.Inject

class GetChannelDataUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(id: String): FetchChannelDataResult {
        val result = channelRepository.fetchChannelData(id)
        if (result is FetchChannelDataResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}