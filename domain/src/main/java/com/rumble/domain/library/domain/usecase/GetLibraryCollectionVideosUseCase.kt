package com.rumble.domain.library.domain.usecase

import androidx.paging.PagingData
import com.rumble.domain.common.domain.usecase.GetVideoPageSizeUseCase
import com.rumble.domain.feed.domain.domainmodel.Feed
import com.rumble.domain.library.domain.model.LibraryCollectionType
import com.rumble.domain.library.model.repository.PlayListRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLibraryCollectionVideosUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    private val getVideoPageSizeUseCase: GetVideoPageSizeUseCase,
) {

    operator fun invoke(libraryCollectionType: LibraryCollectionType): Flow<PagingData<Feed>> {
        val pageSize = getVideoPageSizeUseCase()

        return when (libraryCollectionType) {
            is LibraryCollectionType.Library -> playListRepository.fetchPlayListVideosPaged(
                libraryCollectionType.playListType,
                pageSize = pageSize
            )

            LibraryCollectionType.Purchases -> playListRepository.fetchPurchasesFlow(
                pageSize = pageSize
            )

            is LibraryCollectionType.PlayList -> playListRepository.fetchPlayListVideosPaged(
                id = libraryCollectionType.playListEntity.id,
                pageSize = pageSize
            )
        }
    }
}