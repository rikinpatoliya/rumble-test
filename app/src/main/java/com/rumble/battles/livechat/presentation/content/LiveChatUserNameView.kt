package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6Bold
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedWhite
import com.rumble.theme.liveChatBadgePadding
import com.rumble.theme.liveChatBadgeSize
import com.rumble.theme.radiusXSmall
import com.rumble.utils.extension.toRantCurrencyString
import java.math.BigDecimal

@Composable
fun LiveChatUserNameView(
    modifier: Modifier = Modifier,
    userName: String,
    userBadges: List<String>,
    badges: Map<String, BadgeEntity>,
    textColor: Color = Color.Unspecified,
    textStyle: TextStyle = h6Bold,
    rantPrice: BigDecimal? = null,
    currencySymbol: String = ""
) {
    val annotatedText = buildAnnotatedString {
        append(userName)

        userBadges.forEach {
            appendInlineContent(
                id = it,
                alternateText = badges[it]?.label ?: ""
            )
        }
    }

    val inlineContent = userBadges.associateWith { badgeId ->
        InlineTextContent(
            Placeholder(
                width = (liveChatBadgeSize.value + liveChatBadgePadding.value).sp,
                height = liveChatBadgeSize,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxHeight()
                        .border(width = borderXXSmall, color = enforcedWhite, shape = RoundedCornerShape(radiusXSmall))
                        .align(alignment = Alignment.CenterEnd),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(badges[badgeId]?.url)
                        .build(),
                    contentDescription = badges[badgeId]?.label,
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    Column(modifier = modifier) {
        Text(
            inlineContent = inlineContent,
            text = annotatedText,
            style = textStyle,
            color = textColor
        )

        rantPrice?.let {
            Text(
                text = it.toRantCurrencyString(currencySymbol),
                style = textStyle,
                color = textColor
            )
        }
    }
}

@Composable
@Preview
private fun Preview() {
    RumbleTheme {
        LiveChatUserNameView(
            modifier = Modifier.fillMaxWidth(),
            userName = "TEST",
            badges = emptyMap(),
            userBadges = emptyList(),
            rantPrice = BigDecimal.TEN,
            currencySymbol = "$",
        )
    }
}