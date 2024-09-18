package com.rumble.videoplayer.presentation.internal.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.theme.*
import com.rumble.theme.RumbleTypography.h6
import com.rumble.utils.extension.shortString
import com.rumble.videoplayer.R
import com.rumble.videoplayer.presentation.internal.defaults.buttonRadius

@Composable
@Preview
internal fun LiveChatButton(
    modifier: Modifier = Modifier,
    watchingNumber: Long = 0,
    onClick: () -> Unit = {}
) {
    Box(modifier = modifier
        .clip(RoundedCornerShape(buttonRadius))
        .background(color = enforcedDarkmo)
        .clickable { onClick() }) {
        Row(
            modifier = Modifier
                .padding(
                    top = paddingXSmall,
                    bottom = paddingXSmall,
                    start = paddingSmall,
                    end = paddingSmall
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(paddingXXXSmall)
        ) {

            Image(painter = painterResource(id = R.drawable.ic_message), contentDescription = "")

            Box(
                modifier = Modifier
                    .size(paddingXXXSmall)
                    .clip(CircleShape)
                    .background(fierceRed)
            )

            Text(
                text = watchingNumber.shortString(withDecimal = true),
                color = enforcedWhite,
                style = h6
            )
        }
    }
}
