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
fun RumbleLogoView(modifier: Modifier = Modifier, darkMode: Boolean = MaterialTheme.colors.isLight.not()) {
    Image(
        painter = painterResource(id = if (darkMode) R.drawable.ic_logo_dark else R.drawable.ic_logo),
        contentDescription = stringResource(id = R.string.splash_icon),
        modifier = modifier,
    )
}