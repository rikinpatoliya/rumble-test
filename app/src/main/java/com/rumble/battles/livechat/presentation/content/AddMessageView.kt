package com.rumble.battles.livechat.presentation.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RoundIconButton
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.borderXXSmall
import com.rumble.theme.commentViewHeight
import com.rumble.theme.elevationMedium
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.imageMedium
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants
import com.rumble.utils.RumbleConstants.LIVE_MESSAGE_MAX_CHARACTERS
import com.rumble.utils.extension.conditional

@Composable
fun AddMessageView(
    modifier: Modifier = Modifier,
    message: String,
    placeHolder: String,
    userName: String = "",
    userPicture: String = "",
    rantsEnabled: Boolean,
    maxCharCount: Int = LIVE_MESSAGE_MAX_CHARACTERS,
    onChange: (String) -> Unit = {},
    onBuyRant: () -> Unit = {},
    onSubmit: () -> Unit = {},
    onProfileImageClick: (() -> Unit)? = null,
) {
    var currentCount = message.count()

    Surface(
        modifier = modifier,
        elevation = elevationMedium
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
        ) {
            ConstraintLayout(modifier = Modifier
                .heightIn(min = commentViewHeight)
                .fillMaxWidth()
            ) {
                val (user, addComment) = createRefs()

                ProfileImageComponent(
                    modifier = Modifier
                        .padding(start = paddingSmall, bottom = paddingXSmall, end = paddingXSmall)
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

                Box(
                    modifier = Modifier
                        .padding(bottom = paddingXSmall, top = paddingXSmall, end = paddingSmall)
                        .defaultMinSize(minHeight = imageMedium)
                        .constrainAs(addComment) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(user.end)
                            end.linkTo(parent.end)
                            width = Dimension.fillToConstraints
                        }
                        .clip(RoundedCornerShape(radiusXSmall))
                        .border(
                            borderXXSmall,
                            MaterialTheme.colors.secondaryVariant,
                            RoundedCornerShape(radiusXSmall)
                        )
                ) {
                    BasicTextField(
                        modifier = Modifier
                            .padding(
                                start = paddingMedium,
                                end = paddingMedium,
                                top = paddingXXXSmall,
                                bottom = paddingXXXSmall
                            )
                            .fillMaxWidth()
                            .align(Alignment.CenterStart),
                        value = message,
                        singleLine = false,
                        maxLines = RumbleConstants.MAX_COMMENT_FIELD_LINES,
                        textStyle = RumbleTypography.body1.copy(color = MaterialTheme.colors.primary),
                        cursorBrush = SolidColor(MaterialTheme.colors.primary),
                        onValueChange = {
                            if (it.count() <= maxCharCount) {
                                currentCount = it.count()
                                onChange(it)
                            }
                        })
                    if (message.isEmpty()) {
                        Text(
                            modifier = Modifier
                                .padding(start = paddingMedium)
                                .align(Alignment.CenterStart),
                            text = placeHolder,
                            style = RumbleTypography.body1,
                            color = MaterialTheme.colors.primary.copy(0.5f)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .height(commentViewHeight)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (rantsEnabled)
                    RoundIconButton(
                        modifier = Modifier.padding(start = paddingXXXSmall, end = paddingXXSmall),
                        painter = painterResource(id = R.drawable.ic_dollar),
                        backgroundColor = MaterialTheme.colors.onSurface,
                        tintColor = rumbleGreen,
                        contentDescription = stringResource(id = R.string.send_rant),
                        onClick = onBuyRant
                    )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "$currentCount/$maxCharCount",
                    style = h5,
                    color = MaterialTheme.colors.primaryVariant
                )

                RoundIconButton(
                    modifier = Modifier.padding(start = paddingXXXSmall, end = paddingXXXSmall),
                    painter = painterResource(id = R.drawable.ic_send),
                    backgroundColor = rumbleGreen,
                    tintColor = enforcedDarkmo,
                    contentDescription = placeHolder,
                    onClick = onSubmit
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        AddMessageView(
            message = "",
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
            placeHolder = "placeHolder",
            rantsEnabled = true
        )
    }
}