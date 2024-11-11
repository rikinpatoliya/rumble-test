package com.rumble.domain.livechat.domain.domainmodel

data class EmoteGroupEntity(
    val id: Long,
    val title: String,
    val picture: String?,
    val emoteList: List<EmoteEntity>,
)
