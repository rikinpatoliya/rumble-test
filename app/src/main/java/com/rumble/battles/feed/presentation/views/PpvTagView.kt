package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.rumble.battles.R
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.theme.RumbleTypography.tinyBody10ExtraBold
import com.rumble.theme.enforcedWhite
import com.rumble.theme.newPurple
import com.rumble.theme.paddingXSmall10
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXSmall

@Composable
fun PpvTagView(ppv: PpvEntity?, modifier: Modifier = Modifier) {
    if (ppv != null) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(radiusXSmall))
                .background(newPurple)
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        vertical = paddingXXXSmall,
                        horizontal = paddingXSmall10
                    ),
                text = stringResource(id = R.string.ppv),
                style = tinyBody10ExtraBold,
                color = enforcedWhite
            )
        }
    }
}