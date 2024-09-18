package com.rumble.domain.video.domain.usecases

import android.content.pm.ActivityInfo
import android.media.MediaMetadataRetriever
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.UploadVideoInfo
import com.rumble.domain.camera.domain.usecases.GetMediaFileUriUseCase
import java.io.File
import javax.inject.Inject

private const val TAG = "GetVideoOrientationUseCase"

class GetUploadVideoInfoUseCase @Inject constructor(
    private val getMediaFileUriUseCase: GetMediaFileUriUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
) {
    operator fun invoke(videoUri: String): UploadVideoInfo {
        return try {
            val inputPath = getMediaFileUriUseCase(videoUri)
            File(inputPath).inputStream().use { fileInputStream ->
                val retriever = MediaMetadataRetriever().apply { setDataSource(fileInputStream.fd) }
                val width =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                        ?.toIntOrNull() ?: 0
                val height =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                        ?.toIntOrNull() ?: 0
                val rotation =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                        ?.toIntOrNull() ?: 0
                val duration =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull() ?: 0L
                var orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                if (rotation == 90 || rotation == 270) {
                    // Video is rotated 90 or 270 degrees, so swap dimensions
                    if (width > height)
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    // Video is not rotated or is rotated 180 degrees, use the original dimensions
                    if (height > width)
                        orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                retriever.release()
                UploadVideoInfo(
                    width = width,
                    height = height,
                    orientation = orientation,
                    duration = duration
                )
            }
        } catch (e: Exception) {
            unhandledErrorUseCase(TAG, e)
            UploadVideoInfo(
                width = 0,
                height = 0,
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE,
                duration = 0
            )
        }
    }
}