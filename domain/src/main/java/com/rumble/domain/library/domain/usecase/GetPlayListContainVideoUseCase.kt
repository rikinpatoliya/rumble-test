package com.rumble.domain.library.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.video.PlayListEntity
import javax.inject.Inject


class GetPlayListContainVideoUseCase @Inject constructor() {
    operator fun invoke(playListEntity: PlayListEntity, videoId: Long): Boolean {
        return if (playListEntity.videos.map { video -> video.id }.contains(videoId))
            true
        else playListEntity.videoIds?.contains(videoId) == true
    }

}