package com.rumble.battles.landing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.IsTablet
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.titleLarge
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedGray100
import com.rumble.theme.enforcedGray950
import com.rumble.theme.enforcedWhite
import com.rumble.theme.maxWidthForceUpdateText
import com.rumble.theme.paddingGiant
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXXLarge
import com.rumble.theme.paddingXXXGiant
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional

@Composable
fun ForceUpdateView(
    onGoToStore: () -> Unit
) {
    val configuration = LocalConfiguration.current
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .background(color = enforcedGray950)
            .clickableNoRipple {},
    ) {
        val (image, column) = createRefs()
        Image(
            modifier = Modifier
                .constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(column.top)
                    top.linkTo(parent.top)
                    height = Dimension.fillToConstraints
                }
                .padding(
                    horizontal = CalculatePaddingForTabletWidth(
                        configuration.screenWidthDp.dp
                    )
                )
                .then(Modifier.conditional(IsTablet().not()) {
                    this.padding(top = paddingGiant + paddingXXLarge)
                }),
            painter = painterResource(id = R.drawable.app_update_forced_4x),
            contentDescription = ""
        )
        Column(
            modifier = Modifier
                .constrainAs(column) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(
                    bottom = paddingXXXGiant,
                    start = paddingMedium,
                    end = paddingMedium,
                )
                .widthIn(max = maxWidthForceUpdateText),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(
                        bottom = paddingMedium,
                    ),
                text = stringResource(id = R.string.app_update_required),
                style = titleLarge,
                color = enforcedWhite,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier
                    .padding(
                        bottom = paddingXLarge
                    ),
                text = stringResource(id = R.string.app_update_required_description),
                style = body1,
                color = enforcedGray100,
                textAlign = TextAlign.Center
            )
            ActionButton(
                contentModifier = Modifier
                    .padding(
                        top = paddingMedium,
                        bottom = paddingMedium,
                        start = paddingLarge,
                        end = paddingLarge
                    ),
                text = stringResource(id = R.string.go_to_store),
                backgroundColor = rumbleGreen,
                borderColor = rumbleGreen,
                textColor = enforcedDarkmo,
                textStyle = h3,
                onClick = onGoToStore
            )
        }
    }
}