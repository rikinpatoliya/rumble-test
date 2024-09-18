package com.rumble.domain.library.domain.usecase

import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import javax.inject.Inject

class CanSaveVideoToPlayListUseCase @Inject constructor() {
    operator fun invoke(
        playListEntity: PlayListEntity,
        userId: String,
        userUploadChannels: List<UserUploadChannelEntity>
    ): Boolean {
        var result = false
        if (playListEntity.playListOwnerId == userId)
            result = true
        else {
            userUploadChannels.forEach {
                if (it.id == playListEntity.playListOwnerId) {
                    result = true
                    return@forEach
                }
            }
        }
        return result
    }
}