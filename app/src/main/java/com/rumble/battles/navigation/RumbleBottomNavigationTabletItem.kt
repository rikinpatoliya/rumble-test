package com.rumble.battles.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.battles.R
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.theme.RumbleTypography
import com.rumble.theme.imageSmall
import com.rumble.theme.imageXXMini
import com.rumble.theme.paddingXXSmall

@Composable
fun RumbleBottomNavigationTabletItem(
    contentHandler: ContentHandler,
    selectedTabIndex: Int,
    navItem: NavItem,
    navItemIndex: Int,
) {

    ConstraintLayout {
        val (guide, icon, label, dot) = createRefs()

        Spacer(
            modifier = Modifier
                .width(imageSmall)
                .height(imageXXMini)
                .constrainAs(guide) {
                    centerHorizontallyTo(parent)
                    centerVerticallyTo(parent)
                }
        )

        Text(
            text = stringResource(id = navItem.titleId),
            modifier = Modifier
                .constrainAs(label) {
                    start.linkTo(guide.start)
                    top.linkTo(guide.top)
                    bottom.linkTo(guide.bottom)
                },
            color = getTintColor(selectedTabIndex == navItemIndex),
            style = RumbleTypography.h6
        )

        RumbleBottomNavigationIcon(
            modifier = Modifier
                .constrainAs(icon) {
                    end.linkTo(label.start)
                    top.linkTo(label.top)
                    bottom.linkTo(label.bottom)
                }
                .padding(end = paddingXXSmall),
            contentHandler = contentHandler,
            selectedTabIndex = selectedTabIndex,
            navItem = navItem,
            navItemIndex = navItemIndex
        )

        Image(
            painter = painterResource(id = R.drawable.ic_active_indicator),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(dot) {
                    top.linkTo(guide.bottom)
                    start.linkTo(label.start)
                    end.linkTo(label.start)
                }
                .alpha(
                    getSelectedDotAlpha(selectedTabIndex == navItemIndex)
                )
        )
    }
}