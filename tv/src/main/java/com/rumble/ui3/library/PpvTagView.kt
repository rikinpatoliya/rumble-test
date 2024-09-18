package com.rumble.ui3.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.domain.feed.domain.domainmodel.video.PpvEntity
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedWhite
import com.rumble.theme.newPurple
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXXXSmall

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PpvTagView(ppv: PpvEntity?, modifier: Modifier = Modifier) {
    if (ppv != null) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(radiusXXXSmall))
                .background(newPurple)
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        vertical = paddingXXXSmall,
                        horizontal = paddingXSmall
                    ),
                text = stringResource(id = if (ppv.isPurchased) R.string.video_card_purchased_label else R.string.video_card_ppv_label),
                style = RumbleTypography.h6Heavy,
                color = enforcedWhite
            )
        }
    }
}