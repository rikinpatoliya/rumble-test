package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.model.repository.ChannelRepository
import javax.inject.Inject

class GetChannelDataUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {

    suspend operator fun invoke(id: String): Result<CreatorEntity> =
        channelRepository.fetchChannelData(id)
}