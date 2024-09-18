package com.rumble.domain.video.domain.usecases

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.camera.TrimBitmapData
import com.rumble.domain.camera.domain.usecases.GetMediaFileUriUseCase
import com.rumble.network.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

private const val TAG = "GenerateThumbnailUseCase"

class GenerateThumbnailUseCase @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val getMediaFileUriUseCase: GetMediaFileUriUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase,
) {

    suspend operator fun invoke(
        uri: String,
        time: Long,
        trimBitmapData: TrimBitmapData? = null
    ): Bitmap? {
        return withContext(ioDispatcher) {
            val inputPath = getMediaFileUriUseCase(uri)
            File(inputPath).inputStream().use { fileInputStream ->
                try {
                    val retriever =
                        MediaMetadataRetriever().apply { setDataSource(fileInputStream.fd) }
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        if (trimBitmapData != null) {
                            retriever.getScaledFrameAtTime(
                                time,
                                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                                trimBitmapData.width,
                                trimBitmapData.height
                            )
                        } else {
                            retriever.getFrameAtTime(
                                time,
                                MediaMetadataRetriever.OPTION_CLOSEST_SYNC,
                            )
                        }
                    } else {
                        retriever.getFrameAtTime(time)
                    }
                    retriever.release()
                    bitmap
                } catch (e: Exception) {
                    unhandledErrorUseCase(TAG, e)
                    null
                }
            }
        }
    }
}