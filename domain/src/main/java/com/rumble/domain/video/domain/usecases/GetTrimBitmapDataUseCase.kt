package com.rumble.domain.video.domain.usecases

import android.content.pm.ActivityInfo
import androidx.compose.ui.unit.IntSize
import com.rumble.domain.camera.TrimBitmapData
import javax.inject.Inject

class GetTrimBitmapDataUseCase @Inject constructor(
    private val getUploadVideoInfoUseCase: GetUploadVideoInfoUseCase,
    private val getExtractThumbnailTimesUseCase: GetExtractThumbnailTimesUseCase,
) {
    operator fun invoke(uri: String, size: IntSize): TrimBitmapData {
        val uploadVideoInfo = getUploadVideoInfoUseCase(uri)
        val verticalVideo = uploadVideoInfo.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        val width = if (verticalVideo) size.height * 9 / 16 else size.height * 16 / 9
        val quantity = (size.width / width).plus(1)
        val thumbExtractTimes = getExtractThumbnailTimesUseCase(
            duration = uploadVideoInfo.duration,
            quantity = quantity
        )
        return TrimBitmapData(
            height = if (verticalVideo) size.height else size.height,
            width = width,
            quantity = quantity,
            uploadVideoInfo = uploadVideoInfo,
            thumbExtractTimes = thumbExtractTimes
        )
    }
}