package com.rumble.domain.video.domain.usecases

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toFile
import com.rumble.network.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SaveThumbnailToFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(thumbUrl: String, bitmap: Bitmap) {
        withContext(ioDispatcher) {
            val outputFile = Uri.parse(thumbUrl).toFile()
            outputFile.outputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }
        }
    }

    suspend operator fun invoke(thumbUrl: String, imageUri: Uri) {
        withContext(ioDispatcher) {
            val outputFile = Uri.parse(thumbUrl).toFile()
            context.contentResolver?.openInputStream(imageUri)?.let { input ->
                input.use {
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
        }
    }
}