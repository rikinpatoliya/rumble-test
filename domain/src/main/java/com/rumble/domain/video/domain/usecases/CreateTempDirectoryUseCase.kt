package com.rumble.domain.video.domain.usecases

import android.content.Context
import com.rumble.domain.uploadmanager.UploadManagerConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class CreateTempDirectoryUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(uploadUUID: String): String {
        val outputDirectoryName =
            "${UploadManagerConstants.TEMP_DIRECTORY}_${uploadUUID}"
        val outputDir = File(context.cacheDir, outputDirectoryName)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        return outputDirectoryName
    }
}