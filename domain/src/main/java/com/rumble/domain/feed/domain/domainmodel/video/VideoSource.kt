package com.rumble.domain.feed.domain.domainmodel.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoSource(
    val videoUrl: String,
    val type: String,
    val resolution: Int,
    val bitrate: Int,
    val qualityText: String?,
    val bitrateText: String?
) : Parcelable
