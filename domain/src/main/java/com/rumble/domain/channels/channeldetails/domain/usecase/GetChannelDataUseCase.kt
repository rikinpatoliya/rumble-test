package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.model.repository.ChannelRepository
import javax.inject.Inject

class GetChannelDataUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {

    suspend operator fun invoke(id: String): Result<ChannelDetailsEntity> =
        channelRepository.fetchChannelData(id)
}