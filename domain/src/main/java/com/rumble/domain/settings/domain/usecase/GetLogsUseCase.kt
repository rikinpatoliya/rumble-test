package com.rumble.domain.settings.domain.usecase

import android.content.Context
import com.rumble.domain.analytics.domain.usecases.RumbleErrorUseCase
import com.rumble.domain.common.domain.usecase.RumbleUseCase
import com.rumble.utils.RumbleConstants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class GetLogsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    override val rumbleErrorUseCase: RumbleErrorUseCase,
) : RumbleUseCase {

    operator fun invoke(): String {
        val directory = "${context.filesDir.absolutePath}${RumbleConstants.LOGS_DIR_NAME}/"

        val fileDirectory = File(directory)
        val listOfFiles = fileDirectory.listFiles()
        listOfFiles.reverse()

        var logOutput = ""
        listOfFiles.forEach {
            if (it.name.startsWith(RumbleConstants.LOG_PREFIX)) {
                logOutput += readLogFile(it.absolutePath)
            }
        }

        return logOutput
    }

    private fun readLogFile(filePath: String): String {
        val logFile = File(filePath)
        return logFile.readText(Charsets.UTF_8)
    }
}