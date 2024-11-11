package com.rumble.domain.livechat.model

import com.rumble.domain.livechat.domain.domainmodel.EmoteEntity
import com.rumble.domain.livechat.domain.domainmodel.EmoteGroupEntity
import com.rumble.domain.livechat.model.datasource.local.RoomEmote
import com.rumble.network.dto.livechat.Emote
import com.rumble.network.dto.livechat.EmoteListData

fun Emote.toEmoteEntity(): EmoteEntity =
    EmoteEntity(
        name = name,
        url = file,
        subscribersOnly = subscribersOnly,
    )

fun EmoteListData.toEmoteEntityList(): List<EmoteEntity> =
    items.map { item -> item.emotes.map { it.toEmoteEntity() } }.flatten()

fun EmoteListData.toEmoteGroupList(): List<EmoteGroupEntity> =
    items.map { item ->
        EmoteGroupEntity(
            id = item.id,
            title = item.title,
            picture = item.channelPicture,
            emoteList = item.emotes.map { it.toEmoteEntity() }
        )
    }.filterNot { it.emoteList.isEmpty() }

fun EmoteEntity.toRoomEmote() =
    RoomEmote(
        name = name,
        url = url,
        usageCount = usageCount,
        lastUsageTime = lastUsageTime,
    )

fun RoomEmote.toEmoteEntity() =
    EmoteEntity(
        name = name,
        url = url,
        usageCount = usageCount,
        lastUsageTime = lastUsageTime,
    )