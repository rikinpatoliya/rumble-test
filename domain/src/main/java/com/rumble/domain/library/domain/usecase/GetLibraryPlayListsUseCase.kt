package com.rumble.domain.library.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.PlayListsResult
import com.rumble.domain.library.model.repository.PlayListRepository
import com.rumble.utils.RumbleConstants
import javax.inject.Inject

class GetLibraryPlayListsUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(): PlayListsResult {
        val result = playListRepository.fetchPlayLists(
            pageSize = RumbleConstants.LIBRARY_SHORT_LIST_SIZE
        )
        if (result is PlayListsResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}