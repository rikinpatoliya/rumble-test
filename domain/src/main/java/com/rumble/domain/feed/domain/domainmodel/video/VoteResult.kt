package com.rumble.domain.feed.domain.domainmodel.video

import com.rumble.domain.common.model.RumbleError
import com.rumble.domain.common.model.RumbleResult

data class VoteResult(
    val success: Boolean,
    val updatedFeed: VideoEntity,
    override val rumbleError: RumbleError? = null
) : RumbleResult