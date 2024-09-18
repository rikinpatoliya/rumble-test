package com.rumble.ui3.channel.details.more

import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity


interface BlockStateListener{
    fun updateChannelState(channelDetailsEntity: ChannelDetailsEntity)
    fun channelReported()
}