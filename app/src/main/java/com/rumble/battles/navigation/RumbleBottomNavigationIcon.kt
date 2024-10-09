package com.rumble.battles.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.content.presentation.ContentHandler

@Composable
fun RumbleBottomNavigationIcon(
    modifier: Modifier,
    contentHandler: ContentHandler,
    selectedTabIndex: Int,
    navItem: NavItem,
    navItemIndex: Int,
) {
    val userName by contentHandler.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
    val userPicture by contentHandler.userPictureFlow.collectAsStateWithLifecycle(
        initialValue = ""
    )

    Box(modifier = modifier) {
        when (navItem.root) {
            RumbleScreens.Profile -> {
                if (userName.isBlank() && userPicture.isBlank()) {
                    Icon(
                        painter = painterResource(
                            id = getIconDrawable(
                                selectedTabIndex == navItemIndex,
                                navItem
                            )
                        ),
                        contentDescription = stringResource(id = navItem.titleId),
                        tint = getTintColor(selectedTabIndex == navItemIndex)
                    )
                } else {
                    ProfileImageComponent(
                        profileImageComponentStyle =
                        if (selectedTabIndex == NAV_ITEM_INDEX_ACCOUNT)
                            ProfileImageComponentStyle.CircleImageNavBarIconSelectedStyle()
                        else
                            ProfileImageComponentStyle.CircleImageSmallStyle(),
                        userName = userName,
                        userPicture = userPicture
                    )
                }
            }

            RumbleScreens.Discover, RumbleScreens.Library -> {
                Icon(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInRoot()
                        val size = coordinates.size
                        val offset = Offset(
                            x = position.x + size.width / 2,
                            y = position.y + size.height / 2
                        )
                        if (navItem.root is RumbleScreens.Discover) {
                            contentHandler.onDiscoverIconMeasured(offset)
                        } else {
                            contentHandler.onLibraryIconMeasured(offset)
                        }
                    },
                    painter = painterResource(
                        id = getIconDrawable(
                            selectedTabIndex == navItemIndex,
                            navItem
                        )
                    ),
                    contentDescription = stringResource(id = navItem.titleId),
                    tint = getTintColor(selectedTabIndex == navItemIndex)
                )
            }

            else -> {
                Icon(
                    painter = painterResource(
                        id = getIconDrawable(
                            selectedTabIndex == navItemIndex,
                            navItem
                        )
                    ),
                    contentDescription = stringResource(id = navItem.titleId),
                    tint = getTintColor(selectedTabIndex == navItemIndex)
                )
            }
        }
    }
}