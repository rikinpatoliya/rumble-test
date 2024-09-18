package com.rumble.domain.camera

import com.rumble.domain.R


enum class UploadStatus(val value: Int, val titleId: Int) {
    DRAFT(0, R.string.draft),
    PROCESSING(1, R.string.processing),
    WAITING_WIFI(2, R.string.waiting_wifi),
    WAITING_CONNECTION(3, R.string.waiting_connection),
    EMAIL_VERIFICATION_NEEDED(4, R.string.email_verification_needed),
    UPLOADING(5, R.string.uploading),
    FINALIZING(6, R.string.finalizing),
    UPLOADING_FAILED(7, R.string.uploading_failed),
    UPLOADING_SUCCEEDED(8, R.string.uploading_succeeded);

    companion object {
        fun getByValue(value: Int): UploadStatus =
            when (value) {
                0 -> DRAFT
                1 -> PROCESSING
                2 -> WAITING_WIFI
                3 -> WAITING_CONNECTION
                4 -> EMAIL_VERIFICATION_NEEDED
                5 -> UPLOADING
                6 -> FINALIZING
                7 -> UPLOADING_FAILED
                8 -> UPLOADING_SUCCEEDED
                else -> throw Error("Unsupported UploadStatus!")
            }
    }
}