package com.rumble.domain.channels.channeldetails.domain.domainmodel

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.feed.domain.domainmodel.channel.FreshChannel

sealed class FreshChannelListResult {
    data class Success(val channels: List<FreshChannel>) : FreshChannelListResult()
    data class Failure(val rumbleError: RumbleError) : FreshChannelListResult()
}