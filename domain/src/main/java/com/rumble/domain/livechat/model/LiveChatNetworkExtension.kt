package com.rumble.domain.livechat.model

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.network.dto.livechat.Emote
import com.rumble.network.dto.livechat.EmoteListData

fun Emote.toEmoteEntity(): EmoteEntity =
    EmoteEntity(
        name = name,
        url = file,
        followersOnly = subscribersOnly
    )

fun EmoteListData.toEmoteEntityList(): List<EmoteEntity> =
    items.map { item -> item.emotes.map { it.toEmoteEntity() } }.flatten()