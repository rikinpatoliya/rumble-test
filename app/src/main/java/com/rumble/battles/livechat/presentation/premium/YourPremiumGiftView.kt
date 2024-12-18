package com.rumble.battles.livechat.presentation.premium

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.battles.R
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.domain.livechat.domain.domainmodel.GiftPopupMessageEntity
import com.rumble.domain.livechat.domain.domainmodel.PremiumGiftType
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h5
import com.rumble.theme.borderXXSmall
import com.rumble.theme.giftImageHeight
import com.rumble.theme.giftImageWidth
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXSmall10
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.radiusSmall

@Composable
fun YourPremiumGiftView(
    modifier: Modifier = Modifier,
    giftPopupMessageEntity: GiftPopupMessageEntity,
    onClose: () -> Unit = {}
) {
    val regularStyle = SpanStyle(
        fontFamily = h5.fontFamily,
        fontWeight = h5.fontWeight,
        fontSize = h5.fontSize,
    )
    val boldStyle = SpanStyle(
        fontFamily = h4.fontFamily,
        fontWeight = h4.fontWeight,
        fontSize = h4.fontSize,
    )
    val styledText = buildAnnotatedString {
        withStyle(style = boldStyle) {
            append(giftPopupMessageEntity.giftAuthor)
        }
        withStyle(style = regularStyle) {
            append(" ")
            append(stringResource(R.string.gifted_you_a))
            append(" ")
        }
        withStyle(style = boldStyle) {
            append(getGiftDetails(giftPopupMessageEntity.giftType))
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radiusSmall))
            .background(RumbleCustomTheme.colors.background)
            .border(
                color = RumbleCustomTheme.colors.backgroundHighlight,
                width = borderXXSmall,
                shape = RoundedCornerShape(radiusSmall)
            ),
    ) {
        Row(
            modifier = Modifier.padding(
                start = paddingSmall,
                end = paddingSmall,
                top = paddingXSmall
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_gift),
                contentDescription = stringResource(id = R.string.your_gift),
                tint = RumbleCustomTheme.colors.primaryVariant
            )
            Text(
                modifier = Modifier.padding(start = paddingXXSmall),
                text = stringResource(R.string.your_gift),
                color = RumbleCustomTheme.colors.primaryVariant,
                style = RumbleTypography.h6
            )
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.close),
                modifier = Modifier
                    .clickable { onClose() },
                tint = MaterialTheme.colors.primary
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = paddingSmall,
                    end = paddingSmall,
                    top = paddingXXXSmall,
                    bottom = paddingSmall
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConstraintLayout {
                val (profileImage, gift) = createRefs()
                ProfileImageComponent(
                    modifier = Modifier.constrainAs(profileImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXXMediumStyle(),
                    userName = giftPopupMessageEntity.giftAuthor,
                    userPicture = giftPopupMessageEntity.giftAuthorImage ?: "",
                )
                Image(
                    modifier = Modifier
                        .constrainAs(gift) {
                            end.linkTo(profileImage.end, margin = -paddingXXXSmall)
                            bottom.linkTo(profileImage.bottom, margin = -paddingXXXSmall)
                        }
                        .size(width = giftImageWidth, height = giftImageHeight),
                    painter = painterResource(id = R.drawable.present),
                    contentDescription = stringResource(id = R.string.premium),
                )
            }
            Text(
                modifier = Modifier.padding(start = paddingXSmall10),
                text = styledText,
                color = MaterialTheme.colors.primary,
            )
        }
    }
}

@Composable
private fun getGiftDetails(giftType: PremiumGiftType) =
    when(giftType) {
        PremiumGiftType.SubsGift -> stringResource(R.string.one_month_rumble_subscription)
        PremiumGiftType.PremiumGift -> stringResource(R.string.one_month_channel_subscription)
    }

