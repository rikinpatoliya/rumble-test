package com.rumble.battles.profile.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.imageXXSmall
import com.rumble.theme.paddingMedium
import com.rumble.theme.profileItemIconContentPadding

@Composable
fun ProfileItemView(
    modifier: Modifier = Modifier,
    iconId: Int = 0,
    labelId: Int = 0,
    onClick: () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
    tint: Color? = null
) {
    Box(modifier = Modifier.clickable { onClick() }) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.onPrimary),
            ) {
                Icon(
                    modifier = Modifier
                        .padding(profileItemIconContentPadding)
                        .size(imageXXSmall),
                    painter = painterResource(id = iconId),
                    contentDescription = stringResource(id = labelId),
                    tint = tint ?: MaterialTheme.colors.primary
                )
            }

            Text(
                modifier = Modifier.padding(start = paddingMedium),
                text = stringResource(id = labelId),
                style = RumbleTypography.h3
            )

            Spacer(modifier = Modifier.weight(1F))

            trailingIcon?.invoke()
        }
    }
}

@Composable
@Preview
private fun PreviewProfileItemView() {
    ProfileItemView(
        iconId = R.drawable.ic_settings,
        labelId = R.string.settings
    )
}