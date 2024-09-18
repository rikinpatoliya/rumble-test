package com.rumble.domain.settings.domain.usecase

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.rumble.domain.analytics.domain.usecases.UnhandledErrorUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "ShareLogsUseCase"

class ShareLogsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getLogsUseCase: GetLogsUseCase,
    private val unhandledErrorUseCase: UnhandledErrorUseCase
)  {

    operator fun invoke(): Boolean {
        val logs = getLogsUseCase()
        val timestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now())

        val outputDir: File = context.cacheDir
        val outputFile: File = File.createTempFile("logs-$timestamp", ".txt", outputDir)

        try {
            val fileWriter = FileWriter(outputFile)
            val bufferedWriter = BufferedWriter(fileWriter)
            bufferedWriter.write(logs)
            bufferedWriter.close()
        } catch (e: IOException) {
            unhandledErrorUseCase(TAG, throwable = e)
            return false
        }

        return shareFile(outputFile)
    }

    private fun shareFile(file: File): Boolean {
        val fileUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            clipData = ClipData.newRawUri(null, fileUri)
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "text/plain"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        return try {
            context.startActivity(Intent.createChooser(intent, null)
                .apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
            )
            true
        } catch (e: ActivityNotFoundException) {
            unhandledErrorUseCase(TAG, throwable = e)
            false
        }
    }

}