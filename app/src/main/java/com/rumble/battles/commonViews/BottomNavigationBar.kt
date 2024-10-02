package com.rumble.battles.commonViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.TabBarTag
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.navigation.NAV_ITEM_INDEX_ACCOUNT
import com.rumble.battles.navigation.NavItem
import com.rumble.battles.navigation.NavItems
import com.rumble.battles.navigation.RumbleScreens
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingXXMedium

@Composable
fun BottomNavigationBar(
    contentHandler: ContentHandler,
    selectedTabIndex: Int,
    onNavigationItemClicked: (String, Int) -> Unit,
) {
    RumbleBottomNavigation(
        modifier = Modifier
            .testTag(TabBarTag),
    ) {
        val userName by contentHandler.userNameFlow.collectAsStateWithLifecycle(initialValue = "")
        val userPicture by contentHandler.userPictureFlow.collectAsStateWithLifecycle(
            initialValue = ""
        )

        NavItems.items.forEachIndexed { index, it ->
            BottomNavigationItem(
                selected = selectedTabIndex == index,
                onClick = {
                    onNavigationItemClicked(it.root.rootName, index)
                },
                icon = {
                    ConstraintLayout {
                        val (icon, label, dot, guideLine) = createRefs()

                        Box(modifier = Modifier
                            .constrainAs(icon) {
                                centerHorizontallyTo(parent)
                                bottom.linkTo(label.top)
                            }) {
                            when (it.root) {
                                RumbleScreens.Profile -> {
                                    if (userName.isBlank() && userPicture.isBlank()) {
                                        Icon(
                                            painter = painterResource(
                                                id = getIconDrawable(
                                                    selectedTabIndex == index,
                                                    it
                                                )
                                            ),
                                            contentDescription = stringResource(id = it.titleId),
                                            tint = getTintColor(selectedTabIndex == index)
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
                                            if (it.root is RumbleScreens.Discover) {
                                                contentHandler.onDiscoverIconMeasured(offset)
                                            } else {
                                                contentHandler.onLibraryIconMeasured(offset)
                                            }
                                        },
                                        painter = painterResource(
                                            id = getIconDrawable(
                                                selectedTabIndex == index,
                                                it
                                            )
                                        ),
                                        contentDescription = stringResource(id = it.titleId),
                                        tint = getTintColor(selectedTabIndex == index)
                                    )
                                }

                                else -> {
                                    Icon(
                                        painter = painterResource(
                                            id = getIconDrawable(
                                                selectedTabIndex == index,
                                                it
                                            )
                                        ),
                                        contentDescription = stringResource(id = it.titleId),
                                        tint = getTintColor(selectedTabIndex == index)
                                    )
                                }
                            }
                        }
                        Text(
                            text = stringResource(id = it.titleId),
                            modifier = Modifier
                                .constrainAs(label) {
                                    centerHorizontallyTo(parent)
                                    bottom.linkTo(parent.bottom)
                                },
                            color = getTintColor(selectedTabIndex == index),
                            style = tinyBodySemiBold
                        )
                        Spacer(
                            modifier = Modifier
                                .height(paddingXXMedium)
                                .constrainAs(guideLine) {
                                    centerHorizontallyTo(parent)
                                    top.linkTo(icon.top)
                                }
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_active_indicator),
                            contentDescription = "",
                            modifier = Modifier
                                .constrainAs(dot) {
                                    centerHorizontallyTo(parent)
                                    top.linkTo(guideLine.bottom)
                                }
                                .alpha(
                                    getSelectedDotAlpha(selectedTabIndex == index)
                                )
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun getTintColor(
    selected: Boolean
) =
    if (selected) {
        if (MaterialTheme.colors.isLight) enforcedBlack else enforcedWhite
    } else MaterialTheme.colors.secondary

@Composable
private fun getIconDrawable(
    selected: Boolean,
    it: NavItem
) =
    if (selected) it.iconIdSelected else it.iconId

@Composable
private fun getSelectedDotAlpha(
    selected: Boolean
) = if (selected) 100F else 0F