package com.rumble.domain.settings.domain.usecase

import com.rumble.domain.feed.domain.domainmodel.video.VideoEntity
import com.rumble.domain.livechat.domain.domainmodel.ChatMode
import javax.inject.Inject

class DefineLiveChatModeUseCase @Inject constructor(
    private val hasPremiumRestrictionUseCase: HasPremiumRestrictionUseCase,
) {
    suspend operator fun invoke(videoEntity: VideoEntity, enforcedMode: ChatMode): ChatMode =
        if (hasPremiumRestrictionUseCase(videoEntity).not()) ChatMode.Free else enforcedMode
}