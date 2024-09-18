package com.rumble.domain.channels.channeldetails.domain.domainmodel

import com.rumble.domain.common.model.RumbleError

sealed class ChannelListResult {
    data class Success(val channelList: List<ChannelDetailsEntity>) : ChannelListResult()
    data class Failure(val rumbleError: RumbleError) : ChannelListResult()
}