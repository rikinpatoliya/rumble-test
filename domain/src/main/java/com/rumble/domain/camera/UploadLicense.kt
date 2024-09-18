package com.rumble.domain.camera

import com.rumble.domain.R

enum class UploadLicense(
    val apiValue: Int,
    val titleId: Int,
    val subtitleId: Int,
    val descriptionId: Int
) {
    VIDEO_MANAGEMENT_EXCLUSIVE(
        5,
        R.string.video_management,
        R.string.exclusive,
        R.string.licence_description_video_management_exclusive
    ),
    VIDEO_MANAGEMENT_EXCLUDING_YOUTUBE(
        7,
        R.string.video_management,
        R.string.excluding_youtube,
        R.string.licence_description_video_management_excluding_youtube
    ),
    RUMBLE_ONLY(
        6,
        R.string.rumble_only,
        R.string.non_exclusive_similar_youTube,
        R.string.licence_description_rumble_only
    ),
    PERSONAL_USE(
        0,
        R.string.personal_use,
        R.string.not_monetized_not_searchable_available_to_subscribers,
        R.string.licence_description_personal_use
    );
}