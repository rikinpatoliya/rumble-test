package com.rumble.domain.uploadmanager.workers

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.rumble.domain.uploadmanager.UploadManagerConstants
import java.io.File
import java.util.UUID

class WriteToTempFileWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        val directoryName = inputData.getString(UploadManagerConstants.KEY_DIRECTORY_NAME)
        val resultDataExtra = inputData.getString(UploadManagerConstants.KEY_RESULT_DATA_EXTRA)
        val uriStr = inputData.getString(UploadManagerConstants.KEY_URI)
        val extension = inputData.getString(UploadManagerConstants.KEY_EXTENSION)
        return try {
            if (TextUtils.isEmpty(uriStr)) throw IllegalArgumentException("Invalid uri!")
            val tempFileName = "rumble_temp_${UUID.randomUUID()}.$extension"
            val outputDir = File(applicationContext.cacheDir, "$directoryName")
            if (!outputDir.exists()) {
                outputDir.mkdirs()
            }
            val outputFile = File(outputDir, tempFileName)
            val uri = Uri.parse(uriStr)
            applicationContext.contentResolver?.openInputStream(uri)?.let { input ->
                input.use {
                    outputFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            val outputData = workDataOf(
                UploadManagerConstants.KEY_URI to Uri.fromFile(outputFile).toString(),
                UploadManagerConstants.KEY_EXTENSION to extension,
                UploadManagerConstants.KEY_RESULT_DATA_EXTRA to resultDataExtra,
            )
            Result.success(outputData)
        } catch (t: Throwable) {
            Result.failure()
        }
    }
}