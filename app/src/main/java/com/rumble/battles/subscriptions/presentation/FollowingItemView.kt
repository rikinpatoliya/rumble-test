package com.rumble.battles.subscriptions.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.common.getNotificationIcon
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.SubscriptionStatusActionButton
import com.rumble.battles.commonViews.UserNameViewSingleLine
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.channels.channeldetails.domain.domainmodel.FollowStatus
import com.rumble.domain.channels.channeldetails.domain.domainmodel.UpdateChannelSubscriptionAction
import com.rumble.theme.RumbleTypography.h4
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.RumbleTypography.tinyBody10ExtraBold
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.borderXSmall
import com.rumble.theme.borderXXSmall
import com.rumble.theme.channelActionsButtonWidth
import com.rumble.theme.enforcedWhite
import com.rumble.theme.fierceRed
import com.rumble.theme.imageXXSmall
import com.rumble.theme.liveDotIconSize
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.profileItemIconContentPadding
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXLarge
import com.rumble.theme.radiusXSmall
import com.rumble.theme.verifiedBadgeHeightMedium
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.shortString

@Composable
fun FollowingItemView(
    modifier: Modifier = Modifier,
    channelDetailsEntity: ChannelDetailsEntity,
    onChannelClick: (channelId: String) -> Unit,
    onNotificationClick: () -> Unit,
    onUpdateSubscription: (action: UpdateChannelSubscriptionAction) -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radiusMedium))
            .clickable { onChannelClick(channelDetailsEntity.channelId) },
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (profileImage, liveTag, info, trailingActionBtn) = createRefs()
            Box(
                modifier = Modifier
                    .constrainAs(profileImage) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .conditional(channelDetailsEntity.watchingNowCount != null) {
                        border(
                            width = borderXSmall,
                            color = fierceRed,
                            shape = RoundedCornerShape(radiusXLarge)
                        )
                    }
            ) {
                ProfileImageComponent(
                    profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXXMediumStyle(),
                    userName = channelDetailsEntity.channelTitle,
                    userPicture = channelDetailsEntity.thumbnail
                )
            }
            channelDetailsEntity.watchingNowCount?.let {
                Box(
                    modifier = Modifier
                        .constrainAs(liveTag) {
                            start.linkTo(profileImage.start)
                            end.linkTo(profileImage.end)
                            top.linkTo(profileImage.bottom)
                            bottom.linkTo(profileImage.bottom)
                        }
                        .clip(RoundedCornerShape(radiusXSmall))
                        .background(color = fierceRed)
                ) {
                    Text(
                        modifier = Modifier.padding(
                            horizontal = paddingXXSmall,
                            vertical = borderXXSmall
                        ),
                        text = stringResource(id = R.string.live).uppercase(),
                        color = enforcedWhite,
                        style = tinyBody10ExtraBold
                    )
                }
            }
            Row(
                modifier = Modifier
                    .constrainAs(info) {
                        start.linkTo(profileImage.end)
                        end.linkTo(trailingActionBtn.start)
                        top.linkTo(profileImage.top)
                        bottom.linkTo(profileImage.bottom)
                        width = Dimension.fillToConstraints
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = paddingSmall)
                ) {

                    UserNameViewSingleLine(
                        name = channelDetailsEntity.channelTitle,
                        verifiedBadge = channelDetailsEntity.verifiedBadge,
                        verifiedBadgeHeight = verifiedBadgeHeightMedium,
                        spacerWidth = paddingXXXSmall,
                        textStyle = h4,
                    )

                    Text(
                        text = "${channelDetailsEntity.followers.shortString()} ${
                            pluralStringResource(
                                id = R.plurals.followers, channelDetailsEntity.followers
                            ).lowercase()
                        }",
                        color = MaterialTheme.colors.primaryVariant,
                        style = h6Light
                    )
                }
            }
            Row(
                modifier = Modifier
                    .constrainAs(trailingActionBtn) {
                        end.linkTo(parent.end)
                        top.linkTo(profileImage.top)
                        bottom.linkTo(profileImage.bottom)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                channelDetailsEntity.watchingNowCount?.let { watchingNow ->
                    Box(
                        modifier = Modifier
                            .padding(end = paddingSmall)
                            .clip(RoundedCornerShape(radiusMedium))
                            .background(color = MaterialTheme.colors.onSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = paddingXSmall, vertical = paddingXXSmall),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_dot),
                                contentDescription = "",
                                modifier = Modifier
                                    .size(liveDotIconSize),
                                tint = fierceRed
                            )
                            Text(
                                text = watchingNow.shortString(),
                                modifier = Modifier.padding(start = paddingXXSmall),
                                color = MaterialTheme.colors.primary,
                                style = tinyBodySemiBold
                            )
                        }
                    }
                }
                if (channelDetailsEntity.followed) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colors.onSurface)
                            .clickable { onNotificationClick() },
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(profileItemIconContentPadding)
                                .size(imageXXSmall),
                            painter = painterResource(id = getNotificationIcon(channelDetailsEntity)),
                            contentDescription = "",
                            tint = MaterialTheme.colors.primary
                        )
                    }
                } else {
                    SubscriptionStatusActionButton(
                        modifier = Modifier
                            .widthIn(min = channelActionsButtonWidth)
                            .padding(start = paddingXXXSmall),
                        followStatus = FollowStatus(
                            channelId = channelDetailsEntity.channelId,
                            followed = channelDetailsEntity.followed,
                            isBlocked = channelDetailsEntity.blocked
                        ),
                        onUpdateSubscription = onUpdateSubscription,
                    )
                }
            }
        }
    }
}