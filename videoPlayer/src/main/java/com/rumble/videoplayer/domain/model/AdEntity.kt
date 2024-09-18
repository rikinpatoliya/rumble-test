package com.rumble.videoplayer.domain.model

data class AdEntity(
    val timeCode: VideoAdTimeCode,
    val urlList: MutableList<PreRollUrl>
)