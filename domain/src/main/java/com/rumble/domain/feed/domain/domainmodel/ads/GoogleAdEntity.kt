package com.rumble.domain.feed.domain.domainmodel.ads

import com.rumble.domain.feed.domain.domainmodel.Feed

data class GoogleAdEntity(
    val adUnitId: String,
    override val index: Int = 0,
) : Feed