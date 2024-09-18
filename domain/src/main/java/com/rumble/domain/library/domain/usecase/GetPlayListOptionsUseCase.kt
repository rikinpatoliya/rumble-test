package com.rumble.domain.library.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.library.domain.model.PlayListOption
import com.rumble.network.queryHelpers.PlayListType
import javax.inject.Inject

class GetPlayListOptionsUseCase @Inject constructor() {
    operator fun invoke(
        playListEntity: PlayListEntity,
        userId: String,
    ): List<PlayListOption> {
        val result = mutableListOf<PlayListOption>()
        if (playListEntity.id == PlayListType.WATCH_HISTORY.id)
            result.add(PlayListOption.DeleteWatchHistory)
        else if (userId == playListEntity.playListOwnerId
            && isSystemPlayList(playListEntity.id).not()
            || playListEntity.playListOwnerId == playListEntity.playListChannelEntity?.channelId
        ) {
            result.add(PlayListOption.PlayListSettings)
            result.add(PlayListOption.DeletePlayList)
        }
        return result
    }

    private fun isSystemPlayList(playListId: String): Boolean {
        PlayListType.values().forEach {
            if (it.id == playListId) return true
        }
        return false
    }
}