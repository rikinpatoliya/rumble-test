package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.enforcedWhite
import com.rumble.theme.logoSignInHeight
import com.rumble.theme.logoSignInWidth

@Composable
fun RumbleAuthTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    iconTint: Color = enforcedWhite
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        onBackClick?.let {
            IconButton(
                modifier = Modifier.align(Alignment.CenterStart),
                onClick = it
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back),
                    tint = iconTint
                )
            }
        }

        RumbleLogoView(
            modifier = Modifier
                .align(Alignment.Center)
                .height(logoSignInHeight)
                .width(logoSignInWidth),
            darkMode = true
        )

        onClose?.let {
            IconButton(
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = it
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = stringResource(id = R.string.close),
                    tint = iconTint
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        RumbleAuthTopAppBar(
            onBackClick = {},
            onClose = {}
        )
    }
}