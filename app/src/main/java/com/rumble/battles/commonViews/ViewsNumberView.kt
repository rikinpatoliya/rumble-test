package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import com.rumble.battles.R
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingXXXSmall
import com.rumble.utils.extension.shortString

@Composable
fun ViewsNumberView(
    modifier: Modifier = Modifier,
    painterResourceId: Int = R.drawable.ic_views,
    viewsNumber: Long,
    textStyle: TextStyle = RumbleTypography.h6,
    extraText: String? = null
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(painterResourceId),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primaryVariant)
        )

        Text(
            modifier = Modifier.padding(start = paddingXXXSmall),
            text = "${viewsNumber.shortString()} ${extraText ?: ""}",
            style = textStyle,
            color = MaterialTheme.colors.secondary,
        )
    }
}