package com.rumble.domain.feed.domain.domainmodel.category

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoCategoryEntity(
    val slug: String,
    val title: String,
) : Parcelable