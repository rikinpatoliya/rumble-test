package com.rumble.domain.camera

enum class UploadProgress(val value: Float) {
    STARTED(0.01F),
    SAVED_AND_TRIMMED_VIDEO_FILE(0.06F),
    UPLOAD_THUMB_STARTED(0.07F),
    UPLOAD_THUMB_ENDED(0.1F),
    UPLOAD_VIDEO_STARTED(0.11F),
    //the gap between video upload start and end will be used to report video chunks upload progress
    UPLOAD_VIDEO_ENDED(0.9F),
    MERGE_VIDEO_STARTED(0.91F),
    MERGE_VIDEO_ENDED(0.93F),
    SET_VIDEO_METADATA_STARTED(0.94F),
    SET_VIDEO_METADATA_ENDED(0.96F),
    ENDED(1F);
}