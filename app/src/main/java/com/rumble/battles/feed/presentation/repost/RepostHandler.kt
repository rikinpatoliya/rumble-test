package com.rumble.battles.feed.presentation.repost

import com.rumble.domain.channels.channeldetails.domain.domainmodel.UserUploadChannelEntity
import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.repost.domain.domainmodel.RepostEntity
import com.rumble.videoplayer.player.config.ReportType
import kotlinx.coroutines.flow.StateFlow

interface RepostHandler {
    val repostState: StateFlow<RepostScreenUIState>

    fun onOpenRepostMoreActions(repost: RepostEntity)
    fun onUndoRepost(repostId: Long?)
    fun onDisplayReportRepostOptions(repost: RepostEntity)
    fun onReportRepost(repost: RepostEntity, reportType: ReportType)
    fun onUndoRepostConfirmed(repostId: Long)
    fun onRepostTextChanged(value: String)
    fun onRepostOwnerChanged(repostChannelEntity: UserUploadChannelEntity)
    fun resetRepostState()
    fun onRepost(videoId: Long, channelId: Long, message: String)
}

data class RepostScreenUIState(
    val videoEntity: VideoEntity? = null,
    val post: String = "",
    val repostMaxCharactersError: Boolean = false,
    val repostMinCharactersError: Boolean = false,
    val selectedRepostChannelEntity: UserUploadChannelEntity = UserUploadChannelEntity(),
)