package com.rumble.domain.feed.domain.usecase

import androidx.paging.PagingData
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.model.repository.FeedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLiveFeedUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
) {
    operator fun invoke(): Flow<PagingData<Feed>>  =
        feedRepository.fetchLiveFeedList(pageSize = getVideoPageSizeUseCase())

}