package com.rumble.domain.feed.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoSource
import com.rumble.domain.feed.domain.domainmodel.video.VideoType
import com.rumble.domain.feed.model.repository.FeedRepository
import javax.inject.Inject

class UpdateVideoSourceListUseCase @Inject constructor(
    private val feedRepository: FeedRepository,
) {
    suspend operator fun invoke(video: VideoEntity): List<VideoSource> {
        val videoUrl = video.videoSourceList.first().videoUrl
        val type = getVideoTypeByUrl(videoUrl)
        return if (type == VideoType.M3U8) {
            feedRepository.fetchLiveVideoPlaylist(videoUrl) + video.videoSourceList
        } else {
            video.videoSourceList
        }
    }

    private fun getVideoTypeByUrl(url: String): VideoType {
        return if (url.contains(VideoType.MP4.extension)) VideoType.MP4
        else if (url.contains(VideoType.M3U8.extension)) VideoType.M3U8
        else VideoType.UNKNOWN
    }
}