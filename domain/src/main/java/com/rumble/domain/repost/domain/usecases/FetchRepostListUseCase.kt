package com.rumble.domain.repost.domain.usecases

import androidx.paging.PagingData
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.repost.model.repository.RepostRepository
import com.rumble.utils.extension.getChannelId
import com.rumble.utils.extension.getUserId
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchRepostListUseCase @Inject constructor(
    private val repostRepository: RepostRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
) {
    operator fun invoke(): Flow<PagingData<Feed>> =
        repostRepository.fetchFeedRepostData(pageSize = getVideoPageSizeUseCase())

    operator fun invoke(userId: String, channelId: String?): Flow<PagingData<Feed>> =
        repostRepository.fetchRepostData(
            userId = getUserId(userId, channelId),
            channelId = getChannelId(channelId),
            pageSize = getVideoPageSizeUseCase()
        )

    private fun getChannelId(channelId: String?): String? =
        if (channelId.isNullOrEmpty()) null else channelId.getChannelId().toString()

    private fun getUserId(userId: String, channelId: String?): String? =
        if (channelId.isNullOrEmpty().not()) null else userId.getUserId().toString()
}