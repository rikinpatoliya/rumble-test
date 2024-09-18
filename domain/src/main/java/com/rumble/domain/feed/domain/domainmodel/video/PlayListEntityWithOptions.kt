package com.rumble.domain.feed.domain.domainmodel.video

import com.rumble.domain.library.domain.model.PlayListOption

data class PlayListEntityWithOptions(
    val playListEntity: PlayListEntity,
    val playListOptions: List<PlayListOption>
)