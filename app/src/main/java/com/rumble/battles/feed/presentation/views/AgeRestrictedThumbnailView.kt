package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.battles.commonViews.BlurredImage
import com.rumble.domain.settings.domain.domainmodel.ListToggleViewStyle
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.RumbleTypography.tinyBodySemiBold8dp
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall

@Composable
fun AgeRestrictedThumbnailView(
    modifier: Modifier = Modifier,
    listToggleViewStyle: ListToggleViewStyle,
    url: String = ""
) {
    Box(modifier = modifier) {
        BlurredImage(
            modifier = Modifier.fillMaxSize(),
            url = url
        )

        Text(
            modifier = Modifier
                .padding(if (listToggleViewStyle == ListToggleViewStyle.GRID) paddingSmall else paddingXXSmall)
                .clip(RoundedCornerShape(radiusSmall))
                .background(enforcedDarkmo)
                .padding(horizontal = paddingXSmall, vertical = paddingXXXSmall),
            text = stringResource(id = R.string.age_restricted),
            color = enforcedWhite,
            style = if (listToggleViewStyle == ListToggleViewStyle.GRID) h6 else tinyBodySemiBold8dp
        )
    }
}