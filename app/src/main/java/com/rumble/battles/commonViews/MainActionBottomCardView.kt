package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.rumble.battles.EditProfileMainActionButtonTag
import com.rumble.theme.elevation
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingMedium
import com.rumble.theme.radiusXMedium

@Composable
fun MainActionBottomCardView(
    modifier: Modifier = Modifier,
    title: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        elevation = elevation,
        shape = RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium)
    ) {
        Column {
            MainActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = paddingMedium,
                        start = paddingMedium,
                        end = paddingMedium,
                    )
                    .testTag(EditProfileMainActionButtonTag),
                text = title,
                textColor = enforcedDarkmo,
                onClick = onClick
            )
            BottomNavigationBarScreenSpacer()
        }
    }
}