package com.rumble.battles.livechat.presentation.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.commonViews.LimitAwareInputView
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RoundIconButton
import com.rumble.battles.livechat.presentation.emoji.EmotePickerState
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6Bold
import com.rumble.theme.commentViewHeight
import com.rumble.theme.elevationMedium
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.radiusXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.LIVE_MESSAGE_MAX_CHARACTERS
import com.rumble.utils.extension.conditional

@Composable
fun AddMessageView(
    modifier: Modifier = Modifier,
    message: String,
    selectedPosition: Int,
    placeHolder: String,
    userName: String = "",
    userPicture: String = "",
    rantsEnabled: Boolean = false,
    displayEmotes: Boolean = false,
    maxCharCount: Int = LIVE_MESSAGE_MAX_CHARACTERS,
    emotePickerState: EmotePickerState = EmotePickerState.None,
    focusRequester: FocusRequester? = null,
    onChange: (String, Int) -> Unit = {_, _ ->},
    onBuyRant: () -> Unit = {},
    onSubmit: () -> Unit = {},
    onSelectEmote: () -> Unit = {},
    onProfileImageClick: (() -> Unit)? = null,
    onTextFieldClicked: () -> Unit = {},
) {
    var currentCount = message.count()
    var singleLine by remember { mutableStateOf(true) }

    Surface(
        modifier = modifier,
        elevation = elevationMedium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .heightIn(min = commentViewHeight)
                    .fillMaxWidth()
            ) {
                val (user, addComment, send, count) = createRefs()

                ProfileImageComponent(
                    modifier = Modifier
                        .padding(start = paddingSmall, bottom = paddingSmall, end = paddingXSmall)
                        .conditional(onProfileImageClick != null) {
                            clickable { onProfileImageClick?.invoke() }
                        }
                        .constrainAs(user) {
                            start.linkTo(parent.start)
                            bottom.linkTo(parent.bottom)
                        },
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                    userName = userName,
                    userPicture = userPicture
                )

                LimitAwareInputView(
                    modifier = Modifier
                        .padding(bottom = paddingXSmall, top = paddingXSmall)
                        .constrainAs(addComment) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(user.end)
                            end.linkTo(send.start)
                            width = Dimension.fillToConstraints
                        },
                    text = message,
                    selectedPosition = selectedPosition,
                    placeHolder = placeHolder,
                    maxCharacters = maxCharCount,
                    displayEmotes = displayEmotes,
                    focusRequester = focusRequester,
                    emotePickerState = emotePickerState,
                    onChange = { message, position ->
                        currentCount = message.count()
                        onChange(message, position)
                    },
                    onSingleLine = {
                        singleLine = it
                    },
                    onSelectEmote = onSelectEmote,
                    onTextFieldClicked = onTextFieldClicked,
                )

                if (rantsEnabled && message.isEmpty()) {
                    RoundIconButton(
                        modifier = Modifier
                            .padding(start = paddingXXXSmall, end = paddingXXXSmall, bottom = paddingXXXSmall)
                            .constrainAs(send) {
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            },
                        painter = painterResource(id = R.drawable.ic_dollar),
                        backgroundColor = MaterialTheme.colors.onSurface,
                        tintColor = rumbleGreen,
                        contentDescription = stringResource(id = R.string.send_rant),
                        onClick = onBuyRant
                    )
                } else {
                    RoundIconButton(
                        modifier = Modifier
                            .padding(start = paddingXXXSmall, end = paddingXXXSmall, bottom = paddingXXXSmall)
                            .constrainAs(send) {
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            },
                        painter = painterResource(id = R.drawable.ic_send),
                        backgroundColor = rumbleGreen,
                        tintColor = enforcedWhite,
                        enabled = message.length <= maxCharCount,
                        contentDescription = placeHolder,
                        onClick = onSubmit
                    )
                }

                SymbolCounterView(
                    modifier = Modifier
                        .constrainAs(count) {
                            top.linkTo(parent.top)
                            start.linkTo(send.start)
                            end.linkTo(send.end)
                        }
                        .padding(top = paddingXXSmall),
                    count = currentCount,
                    maxCharCount = maxCharCount,
                    visible = singleLine.not(),
                )
            }
        }
    }
}

@Composable
private fun SymbolCounterView(
    modifier: Modifier = Modifier,
    count: Int,
    maxCharCount: Int,
    visible: Boolean,
) {
    val text = if (count <= maxCharCount) count.toString()
    else "-$count"

    val background = if (count <= maxCharCount) RumbleCustomTheme.colors.backgroundHighlight
    else fierceRed

    val textColor = if (count <= maxCharCount) RumbleCustomTheme.colors.secondary
    else enforcedWhite

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = ExitTransition.None,
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(radiusXXSmall))
                .background(background),
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(vertical = paddingXXXXSmall, horizontal = paddingXXXSmall),
                text = text,
                style = h6Bold,
                color = textColor,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewSymbolCounterView() {
    RumbleTheme {
        Column {
            SymbolCounterView(
                count = 10,
                maxCharCount = 15,
                visible = true,
            )

            SymbolCounterView(
                count = 20,
                maxCharCount = 15,
                visible = true,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        AddMessageView(
            message = "",
            selectedPosition = 0,
            placeHolder = "placeHolder",
            rantsEnabled = true
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewDark() {
    RumbleTheme(darkTheme = true) {
        AddMessageView(
            message = "",
            selectedPosition = 0,
            placeHolder = "placeHolder",
            rantsEnabled = true
        )
    }
}