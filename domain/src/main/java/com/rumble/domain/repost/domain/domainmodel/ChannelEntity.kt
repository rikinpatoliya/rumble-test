package com.rumble.domain.repost.domain.domainmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChannelEntity(
    val id: String,
    val url: String,
    val title: String,
    val name: String,
    val picture: String?,
    val followers: Long,
    val verifiedBadge: Boolean,
    val followed: Boolean,
) : Parcelable