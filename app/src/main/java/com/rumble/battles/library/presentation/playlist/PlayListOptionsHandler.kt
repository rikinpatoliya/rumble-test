package com.rumble.battles.library.presentation.playlist

import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntityWithOptions

interface PlayListOptionsHandler {
    fun onMorePlayListOptions(playListEntityWithOptions: PlayListEntityWithOptions)
    fun onConfirmDeleteWatchHistory()
    fun onDeleteWatchHistory()
    fun onDeletePlayList(playListId: String)
    fun onPlayListSettings(playListEntity: PlayListEntity)
    fun onConfirmDeletePlayList(playListId: String)
}