package com.rumble.battles.commonViews.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState

/**
 * Extension function to show a snackbar with some default value and also to
 * handle creation of RumbleSnackbarVisuals
 *
 * @param message message text
 * @param title optional title text
 * @param duration optional duration to show the snackbar, defaults to Long
 */
suspend fun SnackbarHostState.showRumbleSnackbar(
    message: String,
    title: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Long,
) {
    this.showSnackbar(
        RumbleSnackbarVisuals(
            message = message,
            title = title,
            duration = duration,
            withDismissAction = true,
        )
    )
}