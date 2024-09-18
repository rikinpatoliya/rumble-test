package com.rumble.domain.feed.domain.domainmodel.video

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistVideoEntity(
    val id: Long,
    val title: String,
    val thumbnail: String?,
    val uploadDate: String,
    val numberOfView: Int,
    val watchingNow: Long?,
    val duration: Int,
    val livestreamStatus: Int?,
    val liveDateTime: String?,
    val liveStreamedOn: String?,
    val tags: List<String>?,
    val livestreamHasDvr: Boolean?,
) : Parcelable