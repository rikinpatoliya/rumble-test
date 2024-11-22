package com.rumble.domain.repost.domain.usecases

import androidx.paging.PagingData
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.repost.model.repository.RepostRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchRepostListUseCase @Inject constructor(
    private val repostRepository: RepostRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
) {
    operator fun invoke(): Flow<PagingData<Feed>> =
        repostRepository.fetchFeedRepostData(pageSize = getVideoPageSizeUseCase())

    operator fun invoke(id: String): Flow<PagingData<Feed>> =
        repostRepository.fetchRepostData(id = id, pageSize = getVideoPageSizeUseCase())
}