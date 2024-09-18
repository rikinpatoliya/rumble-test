package com.rumble.domain.library.domain.usecase

import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.UpdatePlayListResult
import com.rumble.domain.library.model.repository.PlayListRepository
import com.rumble.utils.extension.getChannelId
import javax.inject.Inject

class EditPlayListUseCase @Inject constructor(
    private val playListRepository: PlayListRepository,
    override val rumbleErrorUseCase: RumbleErrorUseCase
) : RumbleUseCase {
    suspend operator fun invoke(
        playListEntity: PlayListEntity,
    ): UpdatePlayListResult {
        val result =
            playListRepository.editPlayList(
                playListEntity.id,
                playListEntity.title,
                playListEntity.description,
                playListEntity.visibility.value,
                getChannelId(playListEntity)
            )
        if (result is UpdatePlayListResult.Failure) {
            rumbleErrorUseCase(result.rumbleError)
        }
        return result
    }

    private fun getChannelId(playListEntity: PlayListEntity): Long? =
        if (playListEntity.playListOwnerId == playListEntity.playListUserEntity.id)
            null
        else
            playListEntity.playListOwnerId.getChannelId()
}