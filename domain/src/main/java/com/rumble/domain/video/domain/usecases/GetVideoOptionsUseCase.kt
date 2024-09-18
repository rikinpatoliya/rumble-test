package com.rumble.domain.video.domain.usecases

import com.rumble.domain.common.domain.domainmodel.PlayListResult
import com.rumble.domain.library.domain.usecase.GetPlayListContainVideoUseCase
import com.rumble.domain.library.domain.usecase.GetPlayListUseCase
import com.rumble.domain.video.model.VideoOption
import com.rumble.network.queryHelpers.PlayListType
import javax.inject.Inject

class GetVideoOptionsUseCase @Inject constructor(
    private val getPlayListUseCase: GetPlayListUseCase,
    private val getPlayListContainVideoUseCase: GetPlayListContainVideoUseCase,
) {
    suspend operator fun invoke(
        videoEntityId: Long,
        playListId: String = "",
    ): List<VideoOption> {
        val result = mutableListOf<VideoOption>()
        if (playListId.isNotEmpty()) {
            when (playListId) {
                PlayListType.WATCH_HISTORY.id -> result.add(VideoOption.RemoveFromWatchHistory)
                PlayListType.WATCH_LATER.id -> result.add(VideoOption.RemoveFromWatchLater)
                else -> result.add(VideoOption.RemoveFromPlayList(playListId))
            }
        }
        if (playListId != PlayListType.WATCH_LATER.toString()) {
            if (isInWatchLater(videoEntityId))
                result.add(VideoOption.RemoveFromWatchLater)
            else
                result.add(VideoOption.SaveToWatchLater)
        }
        result.add(VideoOption.SaveToPlayList)
        result.add(VideoOption.Share)
        return result
    }

    private suspend fun isInWatchLater(videoEntityId: Long): Boolean {
        return when (val result = getPlayListUseCase(PlayListType.WATCH_LATER.toString())) {
            is PlayListResult.Failure -> false
            is PlayListResult.Success -> getPlayListContainVideoUseCase(
                result.playList,
                videoEntityId
            )
        }
    }
}