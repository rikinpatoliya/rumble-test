package com.rumble.videoplayer.domain.model

data class VideoAdDataEntity(
    val preRollList: List<AdEntity> = emptyList(),
    val startUrlList: List<String> = emptyList(),
    val viewUrlList: List<String> = emptyList(),
    val pgViewUrlList: List<String> = emptyList()
)
