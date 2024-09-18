package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.model.repository.ChannelRepository
import javax.inject.Inject

data class LogChannelViewUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {

    suspend operator fun invoke(id: String) {
        channelRepository.logChannelView(id)
    }
}