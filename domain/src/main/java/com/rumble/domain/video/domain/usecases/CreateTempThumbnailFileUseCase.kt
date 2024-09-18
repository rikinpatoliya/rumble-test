package com.rumble.domain.video.domain.usecases

import android.content.Context
import android.net.Uri
import com.rumble.domain.uploadmanager.UploadManagerConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class CreateTempThumbnailFileUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(outputDirectoryName: String): String {
        val outputImageFile = File.createTempFile(
            "rumble_image_temp_",
            ".${UploadManagerConstants.IMAGE_DEFAULT_EXTENSION}",
            File(context.cacheDir, outputDirectoryName)
        )
        return Uri.fromFile(outputImageFile).toString()
    }
}