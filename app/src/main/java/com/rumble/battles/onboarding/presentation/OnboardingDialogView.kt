package com.rumble.battles.onboarding.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingPopupType
import com.rumble.theme.RumbleTypography
import com.rumble.theme.borderXXSmall
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXSmall
import com.rumble.theme.radiusXMedium
import com.rumble.theme.rumbleGreen

@Composable
fun OnboardingDialogView(
    modifier: Modifier = Modifier,
    onboardingPopupType: OnboardingPopupType,
    mainActionText: String,
    onNext: () -> Unit,
    stepText: String? = null,
    showSkipAll: Boolean = false,
    onSkipAll: () -> Unit = {},
    showBackIndicator: Boolean,
    onBack: () -> Unit = {},
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(radiusXMedium),
        color = MaterialTheme.colors.background,
        border = BorderStroke(width = borderXXSmall, color = MaterialTheme.colors.onSecondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingMedium)
        ) {
            stepText?.let {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (showBackIndicator) {
                        Icon(
                            modifier = Modifier.clickable { onBack() },
                            painter = painterResource(id = R.drawable.ic_chevron_left),
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colors.primaryVariant
                        )
                    }
                    Spacer(modifier = Modifier.weight(1F))
                    Text(
                        text = it,
                        color = MaterialTheme.colors.primaryVariant,
                        style = RumbleTypography.body1Bold,
                        textAlign = TextAlign.End,
                    )
                }
                Spacer(modifier = Modifier.height(paddingSmall))
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = onboardingPopupType.titleId),
                color = MaterialTheme.colors.primary,
                style = RumbleTypography.h3,
            )
            Spacer(modifier = Modifier.height(paddingXSmall))
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(id = onboardingPopupType.descriptionId),
                color = MaterialTheme.colors.secondary,
                style = RumbleTypography.smallBody,
            )
            Spacer(modifier = Modifier.height(paddingLarge))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                if (showSkipAll) {
                    ActionButton(
                        text = stringResource(id = R.string.skip_all),
                        backgroundColor = MaterialTheme.colors.onSecondary,
                        borderColor = MaterialTheme.colors.onSecondary,
                        textColor = MaterialTheme.colors.primary,
                        onClick = onSkipAll
                    )
                    Spacer(modifier = Modifier.width(paddingMedium))
                }
                ActionButton(
                    text = mainActionText,
                    backgroundColor = rumbleGreen,
                    borderColor = rumbleGreen,
                    textColor = enforcedDarkmo,
                    onClick = onNext
                )
            }
        }
    }
}