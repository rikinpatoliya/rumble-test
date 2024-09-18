package com.rumble.domain.library.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.VideoListResult
import com.rumble.domain.library.domain.model.LibraryCollection
import com.rumble.domain.library.domain.model.LibraryCollectionType
import com.rumble.domain.library.model.repository.PlayListRepository
import com.rumble.utils.RumbleConstants.LIBRARY_SHORT_LIST_SIZE
import javax.inject.Inject

class GetLibraryShortListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(libraryCollection: LibraryCollection): VideoListResult {
        return when (libraryCollection.type) {
            is LibraryCollectionType.Library -> {
                getListById(libraryCollection.type.playListType.id)
            }

            is LibraryCollectionType.PlayList -> {
                getListById(libraryCollection.type.playListEntity.id)
            }

            LibraryCollectionType.Purchases -> {
                when (val result = playListRepository.fetchPurchases(
                    pageSize = LIBRARY_SHORT_LIST_SIZE
                )) {
                    is VideoListResult.Failure -> {
                        rumbleErrorUseCase(result.rumbleError)
                        result
                    }

                    is VideoListResult.Success -> result
                }
            }
        }
    }

    private suspend fun getListById(playListId: String) =
        when (val result = playListRepository.fetchPlayListVideos(
            playListId = playListId,
            pageSize = LIBRARY_SHORT_LIST_SIZE
        )) {
            is VideoListResult.Failure -> {
                rumbleErrorUseCase(result.rumbleError)
                result
            }

            is VideoListResult.Success -> result
        }
}