package com.rumble.battles.feed.presentation.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.rumble.battles.R
import com.rumble.battles.commonViews.ActionButton
import com.rumble.battles.commonViews.BlurredImage
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.tinyBody
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.paddingXXXXLarge

@Composable
fun PremiumOnlyThumbnailView(
    modifier: Modifier = Modifier,
    text: String,
    url: String = "",
    onBack: () -> Unit,
    onSubscribeNow: () -> Unit,
) {
    Box(modifier = modifier) {
        BlurredImage(
            modifier = Modifier.fillMaxSize(),
            url = url
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = enforcedBlack.copy(alpha = 0.72F))
        )

        IconButton(
            modifier = Modifier.align(Alignment.TopStart),
            onClick = onBack
        ) {
            Icon(
                Icons.Filled.ArrowBack,
                contentDescription = stringResource(id = R.string.back),
                tint = enforcedWhite
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = paddingXXXXLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.rumble_premium),
                contentDescription = stringResource(
                    id = R.string.rumble_premium
                )
            )
            Spacer(modifier = Modifier.height(paddingSmall))
            Text(
                text = stringResource(id = R.string.premium_only_content),
                color = enforcedWhite,
                style = h3
            )
            Spacer(modifier = Modifier.height(paddingXXXSmall))
            Text(
                text = text,
                color = enforcedWhite,
                style = tinyBody,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(paddingSmall))
            ActionButton(
                text = stringResource(id = R.string.subscribe_now),
                textColor = enforcedDarkmo,
                onClick = onSubscribeNow
            )
        }
    }
}