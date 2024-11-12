package com.rumble.domain.livechat.domain.usecases

import com.rumble.domain.livechat.domain.domainmodel.EmoteGroupEntity
import com.rumble.utils.RumbleConstants.RUMBLE_EMOTE_PACK_ID
import javax.inject.Inject

class UpdateChannelEmoteLockStateUseCase @Inject constructor() {
    operator fun invoke(
        emoteGroups: List<EmoteGroupEntity>?,
        followsCurrentChannel: Boolean,
        subscribedToChannel: Boolean
    ) =
        emoteGroups?.map { group ->
            group.copy(
                emoteList = group.emoteList
                    .map { emoteEntity ->
                        emoteEntity.copy(
                            locked = (followsCurrentChannel.not() && group.id != RUMBLE_EMOTE_PACK_ID) ||
                                (emoteEntity.subscribersOnly && subscribedToChannel.not() && group.id != RUMBLE_EMOTE_PACK_ID)
                        )
                    }
                    .sortedByDescending {
                        it.locked.not()
                    }
            )
        }
}