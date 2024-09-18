package com.rumble.domain.library.domain.usecase

import androidx.paging.PagingData
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.model.datasource.PlayListRemoteDataSource
import com.rumble.utils.RumbleConstants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLibraryPlayListsPagedUseCase @Inject constructor(
    private val playListRemoteDataSource: PlayListRemoteDataSource,
) {
    operator fun invoke(videoId: Long? = null): Flow<PagingData<PlayListEntity>> {
        return playListRemoteDataSource.fetchPlayListsPaged(
            pageSize = RumbleConstants.PAGINATION_PAGE_SIZE,
            videoIds = videoId?.let { listOf(videoId) }
        )
    }
}