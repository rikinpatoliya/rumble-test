package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R

@Composable
@Preview
fun RumbleLogoView(
    modifier: Modifier = Modifier,
    darkMode: Boolean = MaterialTheme.colors.isLight.not(),
    isPremiumUser: Boolean = false
) {
    Image(
        painter = painterResource(
            id = getLogoIconId(darkMode, isPremiumUser)
        ),
        contentDescription = stringResource(id = R.string.splash_icon),
        modifier = modifier,
    )
}

@Composable
private fun getLogoIconId(darkMode: Boolean, isPremiumUser: Boolean): Int =
    if (isPremiumUser) {
        if (darkMode) R.drawable.ic_logo_premium else R.drawable.ic_logo_premium//TODO: WIP@Kostia - Update when logo for premium dark is provided
    } else {
        if (darkMode) R.drawable.ic_logo_dark else R.drawable.ic_logo
    }
