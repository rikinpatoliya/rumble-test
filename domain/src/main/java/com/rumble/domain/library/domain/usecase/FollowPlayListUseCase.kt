package com.rumble.domain.library.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.common.domain.domainmodel.FollowPlayListResult
import com.rumble.domain.library.model.repository.PlayListRepository
import javax.inject.Inject

class FollowPlayListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(playlistId: String, isFollowing: Boolean): FollowPlayListResult {
        val result = playListRepository.followPlayList(playlistId, isFollowing)
        if (result is FollowPlayListResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }
}