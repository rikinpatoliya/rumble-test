package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import com.rumble.battles.EditProfileMainActionButtonTag
import com.rumble.theme.*

@Composable
fun MainActionBottomCardView(
    modifier: Modifier,
    title: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
            .background(MaterialTheme.colors.background),
        elevation = elevation
    ) {
        MainActionButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = paddingMedium,
                    start = paddingMedium,
                    end = paddingMedium,
                    bottom = paddingXLarge
                )
                .testTag(EditProfileMainActionButtonTag),
            text = title,
            textColor = enforcedDarkmo,
            onClick = onClick
        )
    }
}