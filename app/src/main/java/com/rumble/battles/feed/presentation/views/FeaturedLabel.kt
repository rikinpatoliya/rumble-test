package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.channels.channeldetails.domain.domainmodel.DisplayScreenType
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingXXSmall

@Composable
fun FeaturedLabel(modifier: Modifier = Modifier, displayScreenType: DisplayScreenType) {
    Row(
        modifier = modifier
    ) {
        val text = stringResource(id = R.string.featured)
        if (displayScreenType == DisplayScreenType.CHANNELDETAILS) {
            Icon(
                modifier = Modifier.padding(end = paddingXXSmall),
                painter = painterResource(id = R.drawable.ic_pinned),
                contentDescription = stringResource(id = R.string.featured),
                tint = MaterialTheme.colors.secondary
            )
        }
        Text(
            text = if (displayScreenType == DisplayScreenType.CHANNELDETAILS) text else text.uppercase(),
            style = if (displayScreenType == DisplayScreenType.CHANNELDETAILS) RumbleTypography.h6 else RumbleTypography.tinyBody10ExtraBold,
            color = if (displayScreenType == DisplayScreenType.CHANNELDETAILS) MaterialTheme.colors.secondary else MaterialTheme.colors.primary,
        )
    }

}