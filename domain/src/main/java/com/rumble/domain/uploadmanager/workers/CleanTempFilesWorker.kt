package com.rumble.domain.uploadmanager.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.rumble.domain.uploadmanager.UploadManagerConstants
import java.io.File

class CleanTempFilesWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val directoryName = inputData.getString(UploadManagerConstants.KEY_DIRECTORY_NAME)
        return try {
            val outputDirectory = File(applicationContext.cacheDir, "$directoryName")
            if (outputDirectory.exists()) {
                val files = outputDirectory.listFiles()
                if (files != null) {
                    for (file in files) {
                        val name = file.name
                        if (name.isNotEmpty()) file.delete()
                    }
                }
            }
            Result.success()
        } catch (exception: Exception) {
            Result.failure()
        }
    }
}