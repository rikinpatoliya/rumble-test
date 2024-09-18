package com.rumble.battles.discover.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingMedium
import com.rumble.theme.radiusMedium

@Composable
fun ErrorView(modifier: Modifier, backgroundColor: Color = MaterialTheme.colors.primaryVariant, onRetry: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .background(backgroundColor)
    ) {
        Column(modifier = Modifier.align(Alignment.Center).padding(vertical = paddingMedium)) {
            Text(
                text = stringResource(id = R.string.something_went_wrong),
                style = RumbleTypography.h6
            )
            ActionButton(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = paddingMedium),
                onClick = onRetry,
                text = stringResource(id = R.string.retry)
            )
        }

    }
}