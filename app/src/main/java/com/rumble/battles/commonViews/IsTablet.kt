package com.rumble.battles.commonViews

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun IsTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return isTablet(configuration)
}

private fun isTablet(configuration: Configuration): Boolean =
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        configuration.screenHeightDp > 600
    } else {
        configuration.screenWidthDp > 600
    }
