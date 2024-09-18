package com.rumble.domain.library.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.library.domain.model.DeletePlayListResult
import com.rumble.domain.library.model.repository.PlayListRepository
import javax.inject.Inject

class DeletePlayListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(playlistId: String): DeletePlayListResult {
        val result = playListRepository.deletePlayList(playlistId)
        if (result is DeletePlayListResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}