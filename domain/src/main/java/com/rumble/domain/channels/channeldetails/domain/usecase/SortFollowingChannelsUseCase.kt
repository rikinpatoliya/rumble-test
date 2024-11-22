package com.rumble.domain.channels.channeldetails.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.sort.SortFollowingType
import javax.inject.Inject

class SortFollowingChannelsUseCase @Inject constructor() {
    operator fun invoke(
        sortType: SortFollowingType,
        channelList: List<CreatorEntity>,
    ): List<CreatorEntity> {

        return when (sortType) {
            SortFollowingType.DEFAULT -> channelList
            SortFollowingType.NAME_A_Z -> channelList.sortedBy { it.channelTitle.lowercase() }
            SortFollowingType.NAME_Z_A -> channelList.sortedByDescending { it.channelTitle.lowercase() }
            SortFollowingType.FOLLOWERS_HIGHEST -> channelList.sortedByDescending { it.followers }
            SortFollowingType.FOLLOWERS_LOWEST -> channelList.sortedBy { it.followers }
        }
    }
}

