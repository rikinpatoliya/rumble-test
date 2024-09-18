package com.rumble.domain.feed.domain.domainmodel.ads

import com.rumble.domain.feed.domain.domainmodel.Feed

data class AdEntity(
    val videoThumbnail: String,
    val title: String,
    val type: AdsType,
    val adUrl: String,
    var viewHash: String = "",
    var position: Int = 0,
    var reported: Boolean = false,
    override val index: Int = 0,
) : Feed
