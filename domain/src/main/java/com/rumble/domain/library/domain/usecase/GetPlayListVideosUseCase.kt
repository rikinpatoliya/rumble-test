package com.rumble.domain.library.domain.usecase

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.library.model.repository.PlayListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayListVideosUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
) {
    operator fun invoke(playListId: String, pageSize: Int): Flow<PagingData<Feed>> =
        playListRepository.fetchPlayListVideosPaged(
            id = playListId,
            pageSize = pageSize
        )
}