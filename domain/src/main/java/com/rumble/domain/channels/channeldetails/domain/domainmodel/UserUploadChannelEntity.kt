package com.rumble.domain.channels.channeldetails.domain.domainmodel

data class UserUploadChannelEntity(
    val id: String = "",
    val channelId: Long = 0,
    val title: String = "",
    val name: String = "",
    val followers: Int = 0,
    val thumbnail: String? = null,
)