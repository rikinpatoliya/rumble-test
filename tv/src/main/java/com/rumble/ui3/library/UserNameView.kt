@file:OptIn(ExperimentalTvMaterial3Api::class)

package com.rumble.ui3.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.rumble.R
import com.rumble.theme.enforcedBone
import com.rumble.theme.rumbleGreen

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UserNameView(
    modifier: Modifier = Modifier,
    name: String,
    verifiedBadge: Boolean,
    textStyle: TextStyle,
    textAlign: TextAlign? = null,
    textColor: Color = MaterialTheme.colorScheme.primary,
) {
    val verifiedContentId = "verifiedContentId"
    val annotatedText = buildAnnotatedString {
        append(name)
        if (verifiedBadge) {
            append(" ")
            appendInlineContent(
                id = verifiedContentId,
                alternateText = stringResource(id = R.string.verified_icon_description)
            )
        }
    }

    val inlineContent = mapOf(
        Pair(
            verifiedContentId,
            InlineTextContent(
                Placeholder(
                    width = textStyle.fontSize,
                    height = textStyle.fontSize,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_verified),
                    contentDescription = stringResource(id = R.string.verified_icon_description),
                    tint = rumbleGreen
                )
            }
        )
    )

    Text(
        modifier = modifier,
        text = annotatedText,
        color = textColor,
        style = textStyle,
        inlineContent = inlineContent,
        textAlign = textAlign,
    )
}

@Composable
fun UserNameViewSingleLine(
    modifier: Modifier = Modifier,
    name: String,
    verifiedBadge: Boolean,
    verifiedBadgeHeight: Dp,
    spacerWidth: Dp,
    textStyle: TextStyle,
    textColor: Color = enforcedBone,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        Text(
            modifier = Modifier.weight(1f, fill = false),
            text = name,
            color = textColor,
            style = textStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (verifiedBadge) {
            Spacer(modifier = Modifier.width(spacerWidth))
            Icon(
                modifier = Modifier.height(verifiedBadgeHeight),
                painter = painterResource(id = R.drawable.ic_verified),
                contentDescription = stringResource(id = R.string.verified_icon_description),
                tint = rumbleGreen
            )
        }
    }
}