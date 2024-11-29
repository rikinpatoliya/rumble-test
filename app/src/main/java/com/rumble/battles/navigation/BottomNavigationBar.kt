package com.rumble.battles.navigation

import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.rumble.battles.TabBarTag
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedWhite
import com.rumble.utils.extension.rumbleUitTestTag

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

        NavItems.items.forEachIndexed { navItemIndex, navItem ->
            BottomNavigationItem(
                selected = selectedTabIndex == navItemIndex,
                onClick = {
                    onNavigationItemClicked(navItem.root.rootName, navItemIndex)
                },
                icon = {
                    if (IsTablet()) {
                        RumbleBottomNavigationTabletItem(
                            modifier = Modifier.rumbleUitTestTag(navItem.testTag),
                            contentHandler = contentHandler,
                            selectedTabIndex = selectedTabIndex,
                            navItem = navItem,
                            navItemIndex = navItemIndex
                        )
                    } else {
                        RumbleBottomNavigationPhoneItem(
                            modifier = Modifier.rumbleUitTestTag(navItem.testTag),
                            contentHandler = contentHandler,
                            selectedTabIndex = selectedTabIndex,
                            navItem = navItem,
                            navItemIndex = navItemIndex
                        )
                    }
                }
            )
        }
    }
}

@Composable
internal fun getTintColor(
    selected: Boolean
) =
    if (selected) {
        if (MaterialTheme.colors.isLight) enforcedBlack else enforcedWhite
    } else MaterialTheme.colors.secondary

@Composable
internal fun getIconDrawable(
    selected: Boolean,
    it: NavItem
) =
    if (selected) it.iconIdSelected else it.iconId

@Composable
internal fun getSelectedDotAlpha(
    selected: Boolean
) = if (selected) 100F else 0F