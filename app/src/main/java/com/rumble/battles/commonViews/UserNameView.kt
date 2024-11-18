package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.rumble.battles.R
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.rumbleUitTestTag

@Composable
fun UserNameView(
    modifier: Modifier = Modifier,
    name: String,
    verifiedBadge: Boolean,
    textStyle: TextStyle,
    textAlign: TextAlign? = null,
    textColor: Color = MaterialTheme.colors.primary,
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
    nameAnnotated: AnnotatedString? = null,
    verifiedBadge: Boolean,
    verifiedBadgeHeight: Dp,
    spacerWidth: Dp,
    textStyle: TextStyle,
    textColor: Color = MaterialTheme.colors.primary,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    testTag: String = ""
) {
    Row(
        modifier = modifier
            .rumbleUitTestTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        if (nameAnnotated != null) {
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = nameAnnotated,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = name,
                color = textColor,
                style = textStyle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
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