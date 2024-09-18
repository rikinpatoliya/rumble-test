package com.rumble.battles.commonViews.snackbar

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals

data class RumbleSnackbarVisuals(
    val title: String? = null,
    override val message: String,
    override val actionLabel: String? = null,
    override val duration: SnackbarDuration = SnackbarDuration.Long,
    override val withDismissAction: Boolean = true,
) : SnackbarVisuals
