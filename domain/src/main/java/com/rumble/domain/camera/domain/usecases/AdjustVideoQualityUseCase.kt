package com.rumble.domain.camera.domain.usecases

import android.content.Context
import android.media.MediaMetadataRetriever
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.effect.Presentation
import androidx.media3.transformer.Composition
import androidx.media3.transformer.DefaultEncoderFactory
import androidx.media3.transformer.EditedMediaItem
import androidx.media3.transformer.EditedMediaItemSequence
import androidx.media3.transformer.Effects
import androidx.media3.transformer.ExportException
import androidx.media3.transformer.ExportResult
import androidx.media3.transformer.Transformer
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import com.rumble.domain.settings.domain.domainmodel.UploadQuality
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "AdjustVideoQualityUseCase"

class AdjustVideoQualityUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
) {
    private val transformationScope = CoroutineScope(Dispatchers.Main)

    @OptIn(UnstableApi::class)
    suspend operator fun invoke(
        inputVideoUrl: String,
        outputFilePath: String,
        uploadQuality: UploadQuality
    ): Boolean = suspendCoroutine { continuation ->
        if (shouldChangeQuality(inputVideoUrl, uploadQuality).not()) {
            continuation.resume(false)
        } else {
            val transformerListener: Transformer.Listener =
                object : Transformer.Listener {
                    override fun onCompleted(composition: Composition, result: ExportResult) {
                        continuation.resume(true)
                    }

                    override fun onError(
                        composition: Composition, result: ExportResult,
                        exception: ExportException
                    ) {
                        continuation.resume(false)
                    }
                }

            val mediaItem = MediaItem.Builder()
                .setUri(inputVideoUrl)
                .build()
            val editedMediaItem = EditedMediaItem.Builder(mediaItem)
                .setEffects(
                    Effects(emptyList(), listOf(Presentation.createForHeight(uploadQuality.resolution)))
                )
                .build()
            val editedMediaItems = mutableListOf(editedMediaItem)
            val sequences = listOf(EditedMediaItemSequence(editedMediaItems))
            val composition = Composition.Builder(sequences).build()
            val encoderFactory = DefaultEncoderFactory.Builder(context).build()
            val transformer = Transformer.Builder(context)
                .setEncoderFactory(encoderFactory)
                .addListener(transformerListener)
                .build()
            transformationScope.launch {
                transformer.start(composition, outputFilePath)
            }
        }
    }

    private fun shouldChangeQuality(inputVideoUrl: String, uploadQuality: UploadQuality): Boolean {
        return if (uploadQuality == UploadQuality.QUALITY_FULL)  false
        else {
            try {
                File(inputVideoUrl).inputStream().use { fileInputStream ->
                    val retriever = MediaMetadataRetriever().apply { setDataSource(fileInputStream.fd) }
                    val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toIntOrNull()
                    height != null && height >= uploadQuality.resolution
                }
            } catch (e: Exception) {
                unhandledErrorUseCase(TAG, e)
                false
            }
        }
    }
}
