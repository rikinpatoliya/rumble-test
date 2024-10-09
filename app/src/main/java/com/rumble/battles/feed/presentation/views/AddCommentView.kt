package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.borderXXSmall
import com.rumble.theme.commentViewHeight
import com.rumble.theme.elevation
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.imageMedium
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional

@Composable
fun AddCommentView(
    modifier: Modifier = Modifier,
    comment: String,
    placeHolder: String,
    userName: String = "",
    userPicture: String = "",
    onChange: (String) -> Unit = {},
    onSubmit: () -> Unit = {},
    onProfileImageClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.height(commentViewHeight),
        elevation = elevation
    ) {
        ConstraintLayout {
            val (user, addComment, send) = createRefs()

            ProfileImageComponent(
                modifier = Modifier
                    .padding(start = paddingSmall, bottom = paddingXSmall, end = paddingXSmall)
                    .constrainAs(user) {
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .conditional(onProfileImageClick != null) {
                        clickable { onProfileImageClick?.invoke() }
                    },
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageMediumStyle(),
                userName = userName,
                userPicture = userPicture
            )

            Box(
                modifier = Modifier
                    .padding(bottom = paddingXSmall, top = paddingXSmall)
                    .defaultMinSize(minHeight = imageMedium)
                    .constrainAs(addComment) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(user.end)
                        end.linkTo(send.start)
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
                    value = comment,
                    textStyle = body1.copy(color = MaterialTheme.colors.primary),
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    onValueChange = {
                        onChange(it)
                    })
                if (comment.isEmpty()) {
                    Text(
                        modifier = Modifier
                            .padding(start = paddingMedium)
                            .align(Alignment.CenterStart),
                        text = placeHolder,
                        style = body1,
                        color = MaterialTheme.colors.primary.copy(0.5f)
                    )
                }
            }

            RoundIconButton(
                modifier = Modifier
                    .padding(end = paddingXXXSmall)
                    .constrainAs(send) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                painter = painterResource(id = R.drawable.ic_send),
                backgroundColor = rumbleGreen,
                tintColor = enforcedDarkmo,
                contentDescription = placeHolder,
                onClick = onSubmit
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun Preview() {
    RumbleTheme {
        AddCommentView(
            modifier = Modifier.fillMaxWidth(),
            placeHolder = stringResource(id = R.string.add_comment),
            comment = "",
            userName = "Test user",
            userPicture = ""
        )
    }
}