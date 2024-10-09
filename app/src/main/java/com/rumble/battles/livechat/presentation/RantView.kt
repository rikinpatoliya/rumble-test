package com.rumble.battles.livechat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.LiveChatMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.RantEntity
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXSmall
import com.rumble.theme.progressBarHeight
import com.rumble.theme.radiusXSmall
import com.rumble.theme.rantViewHeight
import com.rumble.utils.extension.toCurrencyString
import java.math.BigDecimal
import java.time.LocalDateTime

@Composable
fun RantView(
    modifier: Modifier = Modifier,
    rantEntity: RantEntity,
    active: Boolean
) {
    Box(
        modifier = modifier
            .height(rantViewHeight)
            .clip(RoundedCornerShape(radiusXSmall))
            .background(MaterialTheme.colors.onSecondary),
        contentAlignment = Alignment.CenterStart,
    ) {
        ConstraintLayout {
            val (image, price, progress, dimView) = createRefs()
            ProfileImageComponent(
                modifier
                    .padding(
                        start = paddingXXXSmall,
                        top = paddingXXXXSmall,
                        bottom = paddingXXXXSmall,
                        end = paddingXXXSmall
                    )
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXSmallStyle(),
                userName = rantEntity.messageEntity.userName,
                userPicture = rantEntity.messageEntity.userThumbnail ?: ""
            )

            rantEntity.messageEntity.rantPrice?.let {
                Text(
                    modifier = Modifier
                        .padding(end = paddingXXXSmall)
                        .constrainAs(price) {
                            start.linkTo(image.end)
                            top.linkTo(image.top)
                            bottom.linkTo(image.bottom)
                        },
                    text = it.toCurrencyString(rantEntity.messageEntity.currencySymbol),
                    style = h6
                )
            }

            rantEntity.messageEntity.background?.let {
                LinearProgressIndicator(
                    modifier = Modifier
                        .height(progressBarHeight)
                        .constrainAs(progress) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(image.bottom)
                            width = Dimension.fillToConstraints
                        },
                    progress = rantEntity.timeLeftPercentage,
                    color = it
                )
            }

            if (active.not()) {
                Box(
                    modifier = Modifier
                        .constrainAs(dimView) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            width = Dimension.fillToConstraints
                            height = Dimension.fillToConstraints
                        }
                        .background(MaterialTheme.colors.background.copy(0.7f))
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewActive() {
    val rantEntity = RantEntity(
        messageEntity = LiveChatMessageEntity(
            messageId = 0,
            userId = 0,
            channelId = null,
            "test",
            "",
            "",
            Color.Blue,
            Color.Cyan,
            Color.Black,
            BigDecimal(12.00),
            LocalDateTime.now(),
            badges = emptyList(),
            currencySymbol = ""
        ),
        timeLeftPercentage = 0.5f
    )
    RumbleTheme {
        RantView(
            rantEntity = rantEntity,
            active = true
        )
    }
}

@Composable
@Preview
private fun PreviewNotActive() {
    val rantEntity = RantEntity(
        messageEntity = LiveChatMessageEntity(
            messageId = 0,
            userId = 0,
            channelId = null,
            "test",
            "",
            "",
            Color.Blue,
            Color.Cyan,
            Color.Black,
            BigDecimal(12.00),
            LocalDateTime.now(),
            badges = emptyList(),
            currencySymbol = ""
        ),
        timeLeftPercentage = 0.5f
    )
    RumbleTheme {
        RantView(
            rantEntity = rantEntity,
            active = false
        )
    }
}