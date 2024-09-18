package com.rumble.battles.commonViews

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.battles.TabBarTag
import com.rumble.battles.navigation.NavItem
import com.rumble.battles.navigation.NavItems
import com.rumble.battles.navigation.RumbleScreens
import com.rumble.theme.RumbleTypography.tinyBodySemiBold
import com.rumble.theme.bottomBarHeight
import com.rumble.theme.paddingXXSmall
import com.rumble.theme.rumbleGreen

@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    userName: String,
    userPicture: String,
    currentRoute: String,
    onNavigationItemClicked: (String) -> Unit,
    onDiscoverIconCenter: ((Offset) -> Unit)? = null,
    onLibraryIconCenter: ((Offset) -> Unit)? = null
) {

    RumbleBottomNavigation(
        modifier = modifier
            .testTag(TabBarTag)
            .fillMaxWidth()
            .height(bottomBarHeight),
        backgroundColor = MaterialTheme.colors.surface
    ) {
        NavItems.items.forEach {
            BottomNavigationItem(
                modifier = Modifier.wrapContentWidth(),
                selected = currentRoute == it.root.rootName,
                onClick = { onNavigationItemClicked(it.root.rootName) },
                icon = {
                    ConstraintLayout(
                        modifier = Modifier
                            .padding(top = paddingXXSmall)
                            .wrapContentWidth(),
                    ) {
                        val (icon, selectedIcon) = createRefs()

                        Box(modifier = Modifier
                            .wrapContentWidth()
                            .constrainAs(icon) {
                                centerHorizontallyTo(parent)
                                bottom.linkTo(selectedIcon.top)
                            }) {
                            when (it.root) {
                                RumbleScreens.Profile -> {
                                    if (userName.isBlank() && userPicture.isBlank()) {
                                        Icon(
                                            painter = painterResource(
                                                id = getIconDrawable(
                                                    currentRoute,
                                                    it
                                                )
                                            ),
                                            contentDescription = stringResource(id = it.titleId),
                                            tint = getTintColor(currentRoute, it)
                                        )
                                    } else {
                                        ProfileImageComponent(
                                            profileImageComponentStyle =
                                            if (currentRoute == it.root.rootName)
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
                                            (if (it.root is RumbleScreens.Discover) {
                                                onDiscoverIconCenter
                                            } else {
                                                onLibraryIconCenter
                                            })?.invoke(
                                                Offset(
                                                    x = position.x + size.width / 2,
                                                    y = position.y + size.height / 2
                                                )
                                            )
                                        },
                                        painter = painterResource(
                                            id = getIconDrawable(
                                                currentRoute,
                                                it
                                            )
                                        ),
                                        contentDescription = stringResource(id = it.titleId),
                                        tint = getTintColor(currentRoute, it)
                                    )
                                }

                                else -> {
                                    Icon(
                                        painter = painterResource(
                                            id = getIconDrawable(
                                                currentRoute,
                                                it
                                            )
                                        ),
                                        contentDescription = stringResource(id = it.titleId),
                                        tint = getTintColor(currentRoute, it)
                                    )
                                }
                            }
                        }
                        Text(
                            text = stringResource(id = it.titleId),
                            modifier = Modifier
                                .padding(bottom = paddingXXSmall)
                                .constrainAs(selectedIcon) {
                                    centerHorizontallyTo(parent)
                                    bottom.linkTo(parent.bottom)
                                },
                            color = getTintColor(currentRoute, it),
                            style = tinyBodySemiBold
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun getTintColor(
    currentRoute: String,
    it: NavItem
) = if (currentRoute == it.root.rootName) rumbleGreen else MaterialTheme.colors.primaryVariant

@Composable
private fun getIconDrawable(
    currentRoute: String,
    it: NavItem
) = if (currentRoute == it.root.rootName) it.iconIdSelected else it.iconId
