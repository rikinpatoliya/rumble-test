package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.theme.*

@Composable
fun FreshChannelPlusButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clickableNoRipple { onClick() }
            .height(imageHeightLarge)
            .width(imageWidthLarge)
            .clip(RoundedCornerShape(radiusXLarge))
            .border(
                width = borderXSmall,
                color = MaterialTheme.colors.primaryVariant,
                shape = RoundedCornerShape(radiusXLarge)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = borderWidth,
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(radiusXLarge)
                )
                .background(color = MaterialTheme.colors.secondaryVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_plus),
                tint = MaterialTheme.colors.primaryVariant,
                contentDescription = stringResource(id = R.string.may_we_recommend_channels)
            )
        }
    }
}
