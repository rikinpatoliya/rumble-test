package com.rumble.domain.channels.channeldetails.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class ChannelResult {
    data class Success(val channel: CreatorEntity) : ChannelResult()
    data class Failure(val rumbleError: RumbleError) : ChannelResult()
}