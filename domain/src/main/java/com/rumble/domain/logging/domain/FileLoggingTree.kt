package com.rumble.domain.logging.domain

import android.content.Context
import android.util.Log
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy
import ch.qos.logback.core.util.FileSize
import ch.qos.logback.core.util.StatusPrinter
import com.rumble.utils.RumbleConstants
import org.slf4j.LoggerFactory
import timber.log.Timber
import java.nio.charset.Charset


class FileLoggingTree(context: Context) : Timber.DebugTree() {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FileLoggingTree::class.java) as Logger
    }

    init {
        val logDirectory: String = context.filesDir.absolutePath + RumbleConstants.LOGS_DIR_NAME
        configureLogger(logDirectory)
        Timber.i("Logs directory: %s", logDirectory)
    }

    private fun configureLogger(logDirectory: String) {
        val loggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
        loggerContext.reset()

        val rollingPolicy = FixedWindowRollingPolicy()
        rollingPolicy.context = loggerContext
        rollingPolicy.fileNamePattern = "$logDirectory/${RumbleConstants.LOG_PREFIX}.%i"
        rollingPolicy.minIndex = 1
        rollingPolicy.maxIndex = RumbleConstants.MAX_LOG_FILES_INDEX

        val triggeringPolicy = SizeBasedTriggeringPolicy<ILoggingEvent>()
        triggeringPolicy.context = loggerContext
        triggeringPolicy.maxFileSize = FileSize.valueOf(RumbleConstants.LOG_FILE_SIZE)
        triggeringPolicy.start()

        val rollingFileAppender = RollingFileAppender<ILoggingEvent>()
        rollingFileAppender.context = loggerContext
        rollingFileAppender.isImmediateFlush = true
        rollingFileAppender.file = "$logDirectory/${RumbleConstants.LOG_PREFIX}"
        rollingFileAppender.rollingPolicy = rollingPolicy
        rollingFileAppender.triggeringPolicy = triggeringPolicy

        rollingPolicy.setParent(rollingFileAppender)
        rollingPolicy.start()

        val encoder = PatternLayoutEncoder()
        encoder.context = loggerContext
        encoder.charset = Charset.forName("UTF-8")
        encoder.pattern = RumbleConstants.LOG_ENCODER_PATTERN
        encoder.start()
        rollingFileAppender.encoder = encoder
        rollingFileAppender.start()

        val root = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME) as Logger
        root.level = Level.DEBUG
        root.addAppender(rollingFileAppender)

        // print any status messages (warnings, etc) encountered in logback config
        StatusPrinter.print(loggerContext)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (priority == Log.VERBOSE) {
            return
        }
        val logMessage = "$tag: $message"
        when (priority) {
            Log.DEBUG -> logger.debug(logMessage)
            Log.INFO -> logger.info(logMessage)
            Log.WARN -> logger.warn(logMessage)
            Log.ERROR -> logger.error(logMessage)
            Log.ASSERT -> logger.debug(logMessage)
        }
    }
}