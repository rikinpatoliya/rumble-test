package com.rumble.ui3.channel.details.more

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity


interface BlockStateListener{
    fun updateChannelState(channelDetailsEntity: CreatorEntity)
    fun channelReported()
}