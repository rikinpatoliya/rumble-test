package com.rumble.domain.logging.domain.usecase

import android.content.Context
import com.rumble.domain.BuildConfig
import com.rumble.domain.logging.domain.FileLoggingTree
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class InitProductionLoggingUseCase
@Inject constructor(
    @ApplicationContext private val context: Context,
) {
    operator fun invoke(canSubmitLogs: Boolean) {
        if (BuildConfig.BUILD_TYPE == "release") {
            Timber.uprootAll()
            if (canSubmitLogs) {
                Timber.plant(FileLoggingTree(context))
            }
        }
    }
}
