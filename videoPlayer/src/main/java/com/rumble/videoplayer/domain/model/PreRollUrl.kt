package com.rumble.videoplayer.domain.model

data class PreRollUrl(
    val url: String,
    val requestedUrlList: List<String> = emptyList(),
    val impressionUrlList: List<String> = emptyList(),
    val pgImpressionUrlList: List<String> = emptyList(),
    val clickUrlList: List<String> = emptyList()
)
