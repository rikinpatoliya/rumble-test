package com.rumble.battles.subscriptions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextAlign
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.rumbleGreen

@Composable
fun SubscriptionsEmptyView(modifier: Modifier, onSearchClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    )
    {
        Text(
            modifier = Modifier
                .padding(horizontal = paddingMedium),
            text = stringResource(id = R.string.you_are_not_following_any_channels),
            style = h3,
            color = MaterialTheme.colors.primary,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .padding(horizontal = paddingMedium, vertical = paddingXXXSmall),
            text = stringResource(id = R.string.search_for_creators),
            style = h5,
            color = MaterialTheme.colors.secondary,
            textAlign = TextAlign.Center
        )
        ActionButton(
            modifier = Modifier
                .padding(top = paddingSmall),
            text = stringResource(id = R.string.search_now),
            borderColor = rumbleGreen,
            backgroundColor = Color.Transparent,
            onClick = onSearchClick,
            textColor = MaterialTheme.colors.primary
        )
    }
}