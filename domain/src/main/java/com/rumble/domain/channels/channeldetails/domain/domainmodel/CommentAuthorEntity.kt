package com.rumble.domain.channels.channeldetails.domain.domainmodel

sealed class CommentAuthorEntity(open val title: String, open val id: String, open val thumbnail: String?, val channelId: Long?) {
    data class SelfAuthor(override val title: String, override val id: String, override val thumbnail: String?) :
        CommentAuthorEntity(
            title = title,
            id = id,
            thumbnail = thumbnail,
            channelId = null
        )

    data class ChannelAuthor(
        val userUploadChannelEntity: UserUploadChannelEntity,
    ) : CommentAuthorEntity(
        title = userUploadChannelEntity.title,
        id = userUploadChannelEntity.id,
        thumbnail = userUploadChannelEntity.thumbnail,
        channelId = userUploadChannelEntity.channelId
    )
}