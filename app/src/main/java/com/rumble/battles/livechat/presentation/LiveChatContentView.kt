package com.rumble.battles.livechat.presentation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rumble.domain.common.domain.usecase.LinkUrl
import com.rumble.domain.livechat.domain.domainmodel.BadgeEntity
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.theme.RumbleTypography
import com.rumble.theme.liveChatBadgePadding
import com.rumble.theme.liveChatBadgeSize
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.theme.wokeGreen
import com.rumble.utils.RumbleConstants
import com.rumble.utils.extension.getBoundingBoxes
import com.rumble.utils.extension.getEmoteName


@Composable
fun LiveChatContentView(
    modifier: Modifier = Modifier,
    liveChatConfig: LiveChatConfig? = null,
    message: String? = null,
    userName: String? = null,
    userBadges: List<String>? = null,
    badges: Map<String, BadgeEntity> = emptyMap(),
    atMentionRange: IntRange? = null,
    userNameColor: Color = MaterialTheme.colors.primary,
    links: ((String) -> List<LinkUrl>)? = null,
    onClick: () -> Unit = {},
    onLinkClick: (String) -> Unit = {},
) {
    val mentioned = atMentionRange?.let { message?.substring(it) }
    val pattern = remember { Regex(RumbleConstants.EMOTE_PATTERN) }
    val words = message?.split(" ") ?: emptyList()
    val emotes = message?.let { pattern.findAll(message).map { it.value }.toList() } ?: emptyList()
    val atTextColor = if (MaterialTheme.colors.isLight) wokeGreen else rumbleGreen
    val atHighlightColor: Color = rumbleGreen.copy(alpha = 0.2f)
    var onDraw: DrawScope.() -> Unit = {}
    var shouldHighlight = false
    var layoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val preAnnotatedText = buildAnnotatedString {
        userName?.let {
            withStyle(SpanStyle().copy(color = userNameColor, fontWeight = FontWeight.Bold)) {
                append(it)
            }
        }

        userBadges?.forEach {
            badges[it]?.label?.let { label ->
                appendInlineContent(
                    id = it,
                    alternateText = label
                )
            }
        }

        append(" ")

        words.forEach { word ->
            if (emotes.contains(word)) {
                liveChatConfig?.emoteList?.find { it.name == word.getEmoteName() }?.let {
                    if (it.followersOnly) {
                        if (userBadges?.any { badge -> RumbleConstants.EMOTE_BADGES.contains(badge) } == true) {
                            appendInlineContent(word, word)
                        } else {
                            append(RumbleConstants.LOCK_EMOTE_CODE)
                        }
                    } else {
                        appendInlineContent(word, word)
                    }
                } ?: run {
                    append(word)
                }
            } else {
                if (word == mentioned && shouldHighlight.not()) {
                    shouldHighlight = true
                    withStyle(
                        SpanStyle().copy(
                            color = atTextColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(word)
                    }
                } else {
                    append(word)
                }
            }
            append(" ")
        }
    }

    val annotatedText = buildAnnotatedString {
        append(preAnnotatedText)
        links?.invoke(preAnnotatedText.text)?.forEach {
            addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = it.start,
                end = it.end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = it.url,
                start = it.start,
                end = it.end
            )
        }
    }

    val inlineContent = userBadges?.associateWith { badgeId ->
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
                        .align(alignment = Alignment.CenterEnd),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(badges[badgeId]?.url)
                        .build(),
                    contentDescription = badges[badgeId]?.label,
                    contentScale = ContentScale.Fit
                )
            }
        }
    } ?: emptyMap()

    val inlineEmotesContent = emotesContent(emotes = emotes, liveChatConfig = liveChatConfig)

    Text(
        modifier = modifier
            .drawBehind { onDraw() }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    layoutResult?.let {
                        val position = it.getOffsetForPosition(offset)
                        val result = annotatedText
                            .getStringAnnotations("URL", position, position)
                            .firstOrNull()
                        if (result != null) {
                            onLinkClick(result.item)
                        } else {
                            if (position <= (userName?.length ?: 0)) onClick()
                        }
                    }
                }
            },
        inlineContent = inlineContent + inlineEmotesContent,
        text = annotatedText,
        style = RumbleTypography.h6Light,
        color = Color.Unspecified,
        onTextLayout = {
            layoutResult = it
            if (shouldHighlight) {
                mentioned?.let { mentioned ->
                    val start = annotatedText.indexOf(mentioned)
                    val end = minOf(start + mentioned.length, annotatedText.length)
                    if (start < end) {
                        layoutResult?.getBoundingBoxes(start, end)?.let { textBounds ->
                            onDraw = {
                                for (bound in textBounds) {
                                    val paddingPx = paddingXXXXSmall.toPx()
                                    drawRoundRect(
                                        color = atHighlightColor,
                                        cornerRadius = CornerRadius(
                                            x = radiusXSmall.toPx(),
                                            y = radiusXSmall.toPx()
                                        ),
                                        topLeft = bound.topLeft.copy(
                                            x = bound.topLeft.x - paddingPx,
                                            y = bound.topLeft.y - paddingPx
                                        ),
                                        size = bound.size.copy(
                                            width = bound.size.width + 2 * paddingPx,
                                            height = bound.size.height + 2 * paddingPx
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}