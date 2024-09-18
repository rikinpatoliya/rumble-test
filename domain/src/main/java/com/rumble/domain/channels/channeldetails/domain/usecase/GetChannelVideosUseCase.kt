package com.rumble.domain.channels.channeldetails.domain.usecase

import androidx.paging.PagingData
import com.rumble.domain.channels.model.repository.ChannelRepository
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.network.queryHelpers.Sort
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChannelVideosUseCase @Inject constructor(
    private val channelRepository: ChannelRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
) {
    operator fun invoke(id: String, sortType: Sort = Sort.DATE): Flow<PagingData<Feed>> =
        channelRepository.fetchChannelVideos(
            id = id,
            sortType = sortType,
            pageSize = getVideoPageSizeUseCase()
        )
}