package com.rumble.domain.common.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.domainmodel.AddToPlaylistResult
import com.rumble.domain.library.model.repository.PlayListRepository
import javax.inject.Inject

class AddToPlaylistUseCase @Inject constructor(
    val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    suspend operator fun invoke(playlistId: String, videoId: Long): AddToPlaylistResult {
        return playListRepository.addVideoToPlaylist(playlistId, videoId)
    }

}