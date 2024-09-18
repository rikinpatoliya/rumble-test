package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.EmptyViewTag
import com.rumble.battles.R
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.rumbleGreen

data class EmptyViewAction(
    val title: String,
    val action: () -> Unit
)

@Composable
fun EmptyView(
    modifier: Modifier = Modifier,
    iconId: Int? = null,
    title: String? = null,
    text: String? = null,
    action: EmptyViewAction? = null
) {

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .background(RumbleCustomTheme.colors.subtleHighlight)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(paddingMedium)
                .testTag(EmptyViewTag),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            iconId?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = title
                )
            }

            title?.let {
                Text(
                    text = title,
                    color = RumbleCustomTheme.colors.primary,
                    style = h3
                )
            }

            text?.let {
                Text(
                    modifier = Modifier.padding(top = paddingXXXSmall),
                    text = text,
                    color = RumbleCustomTheme.colors.secondary,
                    style = h5,
                    textAlign = TextAlign.Center
                )
            }

            action?.let {
                ActionButton(
                    modifier = Modifier.padding(top = paddingMedium),
                    text = it.title,
                    backgroundColor = MaterialTheme.colors.onSecondary,
                    borderColor = rumbleGreen,
                    textColor = RumbleCustomTheme.colors.primary,
                    onClick = it.action
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        EmptyView(
            modifier = Modifier.fillMaxSize(),
            title = "Title",
            text = stringResource(id = R.string.my_videos_have_not_uploaded_description),
            action = EmptyViewAction("Test action") {}
        )
    }
}