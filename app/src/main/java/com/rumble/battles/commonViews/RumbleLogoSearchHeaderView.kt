package com.rumble.battles.commonViews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.battles.R
import com.rumble.theme.RumbleTheme
import com.rumble.theme.borderXXSmall
import com.rumble.theme.followingHeaderIconSize
import com.rumble.theme.logoHeaderHeight
import com.rumble.theme.logoHeaderHeightTablets
import com.rumble.theme.logoHeight
import com.rumble.theme.logoHeightTablet
import com.rumble.theme.logoWidth
import com.rumble.theme.logoWidthTablet
import com.rumble.theme.paddingMedium
import com.rumble.utils.extension.conditional

@Composable
fun RumbleLogoSearchHeaderView(
    modifier: Modifier = Modifier,
    hasUnreadNotifications: Boolean,
    userLoggedIn: Boolean = true,
    onSearch: () -> Unit,
    onNotifications: (() -> Unit),
    onSearchIconGlobalMeasured: ((Offset) -> Unit)? = null,
    onFollowingIconGlobalMeasured: ((Offset) -> Unit)? = null,
    onFollowing: (() -> Unit)? = null,
) {
    val tablet = IsTablet()
    val height = if (tablet) logoHeaderHeightTablets else logoHeaderHeight
    ConstraintLayout(
        modifier = modifier
            .background(MaterialTheme.colors.onPrimary)
            .fillMaxWidth()
            .height(height)
    ) {
        val (logo, mode, line) = createRefs()

        RumbleLogoView(
            modifier = Modifier
                .padding(start = paddingMedium)
                .constrainAs(logo) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
                .size(
                    width = if (tablet) logoWidthTablet else logoWidth,
                    height = if (tablet) logoHeightTablet else logoHeight
                ),
        )

        Row(modifier = Modifier
            .constrainAs(mode) {
                end.linkTo(parent.end)
                top.linkTo(logo.top)
                bottom.linkTo(logo.bottom)
            }) {
            IconButton(
                onClick = { onSearch() }) {
                Icon(
                    modifier = Modifier
                        .conditional(onSearchIconGlobalMeasured != null) {
                            onGloballyPositioned {
                                val position = it.positionInRoot()
                                val size = it.size
                                onSearchIconGlobalMeasured?.invoke(
                                    Offset(
                                        x = position.x + size.width / 2,
                                        y = position.y + size.height / 2
                                    )
                                )
                            }
                        },
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = stringResource(id = R.string.search_rumble),
                )
            }

            if (userLoggedIn) {
                NotificationIconView(
                    showDot = hasUnreadNotifications,
                    onClick = onNotifications
                )

                if (onFollowing != null) {
                    IconButton(
                        onClick = onFollowing
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(followingHeaderIconSize)
                                .conditional(onFollowingIconGlobalMeasured != null) {
                                    onGloballyPositioned {
                                        val position = it.positionInRoot()
                                        val size = it.size
                                        onFollowingIconGlobalMeasured?.invoke(
                                            Offset(
                                                x = position.x + size.width / 2,
                                                y = position.y + size.height / 2
                                            )
                                        )
                                    }
                                },
                            painter = painterResource(id = R.drawable.ic_subscriptions),
                            contentDescription = stringResource(id = R.string.following),
                        )
                    }
                }
            }
        }

        if (tablet) {
            Divider(
                Modifier
                    .height(borderXXSmall)
                    .fillMaxWidth()
                    .constrainAs(line) { bottom.linkTo(parent.bottom) },
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
}

@Preview
@Composable
private fun PreviewRumbleLogoSearchHeaderView() {
    RumbleLogoSearchHeaderView(
        hasUnreadNotifications = true,
        userLoggedIn = true,
        onSearch = { },
        onNotifications = {},
        onFollowing = {},
    )
}

@Preview
@Composable
private fun PreviewRumbleLogoSearchHeaderViewNotLoggedIn() {
    RumbleTheme {
        RumbleLogoSearchHeaderView(
            hasUnreadNotifications = true,
            userLoggedIn = false,
            onSearch = { },
            onNotifications = {},
            onFollowing = {},
        )
    }
}