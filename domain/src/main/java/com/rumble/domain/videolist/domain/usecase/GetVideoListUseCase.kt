package com.rumble.domain.videolist.domain.usecase

import androidx.paging.PagingData
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.feed.model.repository.FeedRepository
import com.rumble.domain.library.domain.usecase.GetLibraryCollectionVideosUseCase
import com.rumble.domain.videolist.domain.model.VideoList
import com.rumble.domain.videolist.domain.model.VideoListType
import com.rumble.domain.videolist.model.repository.VideoListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVideoListUseCase @Inject constructor(
    private val videoListRepository: VideoListRepository,
    private val feedRepository: FeedRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
    private val getLibraryCollectionVideosUseCase: GetLibraryCollectionVideosUseCase,
) {

    operator fun invoke(list: VideoList): Flow<PagingData<Feed>> {
        val pageSize = getVideoPageSizeUseCase()
        
        return when (list.type) {
            is VideoListType.Collection -> videoListRepository.fetchVideos(
                id = list.type.id.value,
                pageSize = pageSize
            )
            is VideoListType.PlayList -> getLibraryCollectionVideosUseCase(list.type.libraryCollection.type)
            VideoListType.Live -> feedRepository.fetchLiveFeedList(pageSize = pageSize)
            VideoListType.Popular -> videoListRepository.fetchBattlesVideos(
                leaderboard = true,
                pageSize = pageSize
            )
            VideoListType.Battles -> videoListRepository.fetchBattlesVideos(
                leaderboard = false,
                pageSize = pageSize
            )
        }
    }
}