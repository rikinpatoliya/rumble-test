package com.rumble.battles.commonViews

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall

@Composable
fun RumbleTextActionButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = MaterialTheme.colors.primary,
    textStyle: TextStyle = RumbleTypography.h5,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusSmall))
            .clickable { onClick.invoke() }
    ) {
        Text(
            modifier = Modifier
                .padding(
                    start = paddingXSmall,
                    top = paddingXXXSmall,
                    end = paddingXSmall,
                    bottom = paddingXXXSmall
                )
                .align(Alignment.Center),
            text = text,
            style = textStyle,
            color = textColor,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
@Preview
fun PreviewRumbleTextActionButton() {
    RumbleTextActionButton(
        text = stringResource(id = R.string.view_all),
    )
}