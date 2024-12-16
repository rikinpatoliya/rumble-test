package com.rumble.videoplayer.presentation.utils

import androidx.compose.ui.focus.FocusRequester
import timber.log.Timber

/**
 * Safely requests focus for the given FocusRequester.
 * Catches and logs IllegalStateException to prevent application crashes.
 */
fun FocusRequester.requestFocusSafely() {
    try {
        this.requestFocus()
        Timber.d("Focus requested successfully for FocusRequester: $this.")
    } catch (error: IllegalStateException) {
        Timber.e(error, "Failed to request focus for FocusRequester: $this.")
    }
}
