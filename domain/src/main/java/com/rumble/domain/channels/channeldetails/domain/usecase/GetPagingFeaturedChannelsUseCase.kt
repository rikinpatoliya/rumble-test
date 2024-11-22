package com.rumble.domain.channels.channeldetails.domain.usecase

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.rumble.domain.channels.channeldetails.domain.domainmodel.CreatorEntity
import com.rumble.domain.channels.model.repository.ChannelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPagingFeaturedChannelsUseCase @Inject constructor(
    private val channelRepository: ChannelRepository
) {

    operator fun invoke(scope: CoroutineScope): Flow<PagingData<CreatorEntity>> {
        val pagingDataFlow = channelRepository.pagingOfFeaturedChannels()
        val followUpdateFlow = channelRepository.fetchChannelFollowUpdates()

        return pagingDataFlow.cachedIn(scope).combine(followUpdateFlow) { pagingData, channelFollowUpdateList ->
            pagingData.map { channelDetailsEntity ->
                val match = channelFollowUpdateList.find { it.channelId == channelDetailsEntity.channelId }
                match?.let { channelDetailsEntity.copy(followed = it.followed) } ?: channelDetailsEntity
            }
        }
    }
}