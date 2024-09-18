package com.rumble.ui3.home.model

import com.rumble.domain.feed.domain.domainmodel.Feed

class VideoViewAllEntity(
    val feed_id: String,
    val feed_title: String,
    override val index: Int,
) : Feed