package com.rumble.battles.commonViews

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.rumble.battles.R
import com.rumble.battles.livechat.presentation.emoji.EmotePickerState
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.borderXXSmall
import com.rumble.theme.imageMedium
import com.rumble.theme.imageXSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.utils.RumbleConstants.LIVE_MESSAGE_MAX_CHARACTERS
import com.rumble.utils.RumbleConstants.MAX_COMMENT_FIELD_LINES
import com.rumble.utils.extension.conditional

@Composable
fun LimitAwareInputView(
    modifier: Modifier = Modifier,
    text: String = "",
    placeHolder: String = "",
    selectedPosition: Int = 0,
    maxLines: Int = MAX_COMMENT_FIELD_LINES,
    maxCharacters: Int = LIVE_MESSAGE_MAX_CHARACTERS,
    displayEmotes: Boolean = false,
    emotePickerState: EmotePickerState = EmotePickerState.None,
    focusRequester: FocusRequester? = null,
    onChange: (String, Int) -> Unit = { _, _ -> },
    onSingleLine: (Boolean) -> Unit = {},
    onSelectEmote: () -> Unit = {},
    onTextFieldClicked: () -> Unit = {},
) {
    var textState by remember {
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(selectedPosition)
            )
        )
    }
    val source = remember { MutableInteractionSource() }
    val clicked by source.collectIsPressedAsState()

    LaunchedEffect(text) {
        if (text != textState.text) {
            textState = TextFieldValue(text = text, selection = TextRange(selectedPosition))
        }
    }

    LaunchedEffect(clicked) {
        onTextFieldClicked()
    }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = imageMedium)
            .clip(RoundedCornerShape(radiusXSmall))
            .border(
                borderXXSmall,
                MaterialTheme.colors.secondaryVariant,
                RoundedCornerShape(radiusXSmall)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = paddingXSmall, vertical = paddingXXXSmall)
                .fillMaxWidth()
                .align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                modifier = Modifier
                    .weight(1f)
                    .conditional(focusRequester != null) {
                        focusRequester(focusRequester as FocusRequester)
                    },
                interactionSource = source,
                value = textState,
                singleLine = false,
                maxLines = maxLines,
                cursorBrush = SolidColor(RumbleCustomTheme.colors.primary),
                onValueChange = { newText ->
                    textState = newText
                    onChange(newText.text, newText.selection.end)
                },
                visualTransformation = {
                    TransformedText(
                        text = buildInputAnnotation(textState.text, maxCharacters),
                        OffsetMapping.Identity
                    )
                },
                onTextLayout = { textLayoutResult ->
                    textLayoutResult.lineCount
                    onSingleLine(textLayoutResult.lineCount == 1)
                },
                decorationBox = { innerTextField ->
                    Row {
                        Box(modifier = Modifier.padding(vertical = paddingXXSmall)) {
                            innerTextField()
                        }
                    }
                },
            )

            AnimatedVisibility(
                modifier = Modifier.padding(start = paddingXSmall),
                visible = displayEmotes,
                enter = fadeIn(),
                exit = ExitTransition.None,
            ) {
                Box(
                    modifier = Modifier
                        .size(imageMedium)
                        .clip(CircleShape)
                        .conditional(emotePickerState == EmotePickerState.Selected) {
                            background(RumbleCustomTheme.colors.backgroundHighlight)
                        }
                ) {
                    IconButton(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(imageXSmall),
                        onClick = onSelectEmote,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_emoji),
                            contentDescription = stringResource(R.string.select_emote),
                            tint = if (emotePickerState == EmotePickerState.None)
                                MaterialTheme.colors.primary.copy(0.5f)
                            else RumbleCustomTheme.colors.secondary
                        )
                    }
                }
            }
        }

        if (text.isEmpty()) {
            Text(
                modifier = Modifier
                    .padding(start = paddingXSmall)
                    .align(Alignment.CenterStart),
                text = placeHolder,
                style = body1,
                color = MaterialTheme.colors.primary.copy(0.5f)
            )
        }
    }
}

private fun buildInputAnnotation(text: String, maxCharacters: Int): AnnotatedString {
    val overflow = text.length - maxCharacters
    return buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = RumbleCustomTheme.colors.primary,
                fontFamily = body1.fontFamily,
                fontSize = body1.fontSize,
                fontWeight = body1.fontWeight,
            )
        ) {
            append(text.take(maxCharacters))
        }

        if (overflow > 0) {
            withStyle(
                style = SpanStyle(
                    color = RumbleCustomTheme.colors.secondaryVariant,
                    fontFamily = body1.fontFamily,
                    fontSize = body1.fontSize,
                    fontWeight = body1.fontWeight,
                )
            ) {
                append(text.takeLast(overflow))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewEmpty() {
    RumbleTheme {
        LimitAwareInputView(
            placeHolder = stringResource(R.string.add_message)
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewMessage() {
    RumbleTheme {
        LimitAwareInputView(
            text = "This is a message. This is a message. This is a message. This is a message. This is a message. This is a message. This is a message This is a message This is a message This is a message This is a message",
            placeHolder = stringResource(R.string.add_message),
            displayEmotes = true,
        )
    }
}

