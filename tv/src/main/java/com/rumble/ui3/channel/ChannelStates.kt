package com.rumble.ui3.channel

import androidx.paging.PagingData
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity

object ChannelStates {
    var lastSelectedItemPosition: Int = -1
    var reloadChannelData: Boolean = false
    var channelPagingDataMap: PagingData<CreatorEntity>? = null
}