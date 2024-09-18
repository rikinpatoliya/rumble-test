package com.rumble.domain.camera

import com.rumble.domain.R

enum class UploadScheduleOption(
    val titleId: Int,
    val selectedTitleId: Int,
    val selectedSubtitleId: Int
) {
    NOW(R.string.now, R.string.publish_now, R.string.video_available_right_away),
    CHOOSE(R.string.choose_date_time, R.string.publish_specific_time, 0),
}

data class UploadSchedule(
    val option: UploadScheduleOption,
    val subTitle: String = "",
    val utcMillis: Long = 0L
)