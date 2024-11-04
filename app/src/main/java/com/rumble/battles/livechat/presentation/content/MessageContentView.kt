package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.rumble.domain.livechat.domain.domainmodel.LiveChatConfig
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.EMOTE_BADGES
import com.rumble.utils.RumbleConstants.LOCK_EMOTE_CODE
import com.rumble.utils.extension.getBoundingBoxes
import com.rumble.utils.extension.getEmoteName

@Composable
fun MessageContentView(
    modifier: Modifier,
    messageEntity: LiveChatMessageEntity,
    liveChatConfig: LiveChatConfig?,
    style: TextStyle,
    color: Color = MaterialTheme.colors.primary,
    atTextColor: Color,
    atHighlightColor: Color,
) {
    val pattern = remember { Regex(RumbleConstants.EMOTE_PATTERN) }
    val message = messageEntity.notification ?: messageEntity.message
    val words = pattern.split(message)
    val emotes = pattern.findAll(message).map { it.value }.toList()

    val annotatedText = buildAnnotatedString {
        if (emotes.isEmpty()) append(message)
        else {
            val inlineEmotes = emotes.toMutableList()
            words.forEach { word ->
                append(word)
                inlineEmotes.removeFirstOrNull()?.let { emote ->
                    liveChatConfig?.emoteList?.find { it.name == emote.getEmoteName() }?.let {
                        if (it.followersOnly) {
                            if (messageEntity.badges.any { badge -> EMOTE_BADGES.contains(badge) }) {
                                appendInlineContent(emote, emote)
                            } else {
                                append(LOCK_EMOTE_CODE)
                            }
                        } else {
                            appendInlineContent(emote, emote)
                        }
                    } ?: run {
                        append(emote)
                    }
                }
            }
        }
        messageEntity.atMentionRange?.let {
            val start =  it.first
            val end = minOf(it.last + 1, length)
            if (start <= end) {
                addStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.SemiBold,
                        color = atTextColor
                    ),
                    start = start,
                    end = end
                )
            }
        }
    }

    val inlineEmotesContent = emotesContent(emotes = emotes, liveChatConfig = liveChatConfig)

    var onDraw: DrawScope.() -> Unit = {}

    Text(
        modifier = modifier.drawBehind { onDraw() },
        text = annotatedText,
        inlineContent = inlineEmotesContent,
        style = style,
        color = color,
        onTextLayout = { layoutResult ->
            messageEntity.atMentionRange?.let {
                val start = it.first
                val end = minOf(it.last + 1, annotatedText.length -1)
                if (start < end) {
                    val textBounds = layoutResult.getBoundingBoxes(start, end)
                    onDraw = {
                        for (bound in textBounds) {
                            val paddingPx = paddingXXXXSmall.toPx()
                            drawRoundRect(
                                color = atHighlightColor,
                                cornerRadius = CornerRadius(x = radiusXSmall.toPx(), y = radiusXSmall.toPx()),
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
    )
}
