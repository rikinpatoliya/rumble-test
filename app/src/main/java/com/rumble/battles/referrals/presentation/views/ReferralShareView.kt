package com.rumble.battles.referrals.presentation.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.RumbleTextActionButton
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h6
import com.rumble.theme.bottomBarMinimalSpacerBehind
import com.rumble.theme.buttonHeight
import com.rumble.theme.copyLinkHeight
import com.rumble.theme.elevationMedium
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXMedium

@Composable
fun ReferralShareView(
    modifier: Modifier = Modifier,
    referralUrl: String,
    onShare: (title: String, text: String) -> Unit,
    onCopy: () -> Unit
) {
    val clipboard: ClipboardManager = LocalClipboardManager.current

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium),
        elevation = elevationMedium,
        contentColor = MaterialTheme.colors.primary
    ) {
        Column(Modifier.padding(top = paddingLarge)) {
            Row(
                modifier = Modifier
                    .padding(horizontal = paddingMedium)
                    .height(copyLinkHeight)
                    .background(
                        color = MaterialTheme.colors.onSecondary,
                        shape = RoundedCornerShape(radiusMedium)
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = paddingMedium),
                    text = referralUrl,
                    style = RumbleTypography.body1,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                RumbleTextActionButton(
                    modifier = Modifier
                        .padding(horizontal = paddingXSmall),
                    text = stringResource(id = R.string.copy),
                    textStyle = h6
                ) {
                    clipboard.setText(AnnotatedString(referralUrl))
                    onCopy.invoke()
                }
            }

            val shareTitle = stringResource(id = R.string.share)
            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingMedium)
                    .height(buttonHeight),
                text = stringResource(id = R.string.invite_friends),
                textStyle = RumbleTypography.h3,
                textColor = enforcedDarkmo,
                onClick = { onShare.invoke(shareTitle, referralUrl) }
            )
            BottomNavigationBarScreenSpacer(bottomBarMinimalSpacerBehind)
        }
    }
}