package com.rumble.battles.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.rumble.theme.paddingXXMedium

@Composable
fun RumbleBottomNavigationPhoneItem(
    contentHandler: ContentHandler,
    selectedTabIndex: Int,
    navItem: NavItem,
    navItemIndex: Int,
) {

    ConstraintLayout {
        val (icon, label, dot, guideLine) = createRefs()

        RumbleBottomNavigationIcon(
            modifier = Modifier
                .constrainAs(icon) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(label.top)
                },
            contentHandler = contentHandler,
            selectedTabIndex = selectedTabIndex,
            navItem = navItem,
            navItemIndex = navItemIndex
        )

        Text(
            text = stringResource(id = navItem.titleId),
            modifier = Modifier
                .constrainAs(label) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom)
                },
            color = getTintColor(selectedTabIndex == navItemIndex),
            style = RumbleTypography.tinyBodySemiBold
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
                    getSelectedDotAlpha(selectedTabIndex == navItemIndex)
                )
        )
    }
}