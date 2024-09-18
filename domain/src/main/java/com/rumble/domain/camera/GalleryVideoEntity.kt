package com.rumble.domain.camera

import android.net.Uri
import com.rumble.utils.RumbleConstants.RUMBLE_VIDEO_EXTENSION

data class GalleryVideoEntity(
    val galleryVideoId: Long = 0,
    val videoUri: Uri,
    val title: String = "",
    val duration: Int,
    val extension: String = RUMBLE_VIDEO_EXTENSION,
)
