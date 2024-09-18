package com.rumble.battles.library.presentation.views

import android.annotation.SuppressLint
import android.view.WindowInsetsController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import coil.size.Size
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.rumble.battles.R
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.onboardingTitle
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedDarkest
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedWhite
import com.rumble.theme.modalMaxWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXXGiant

@SuppressLint("InlinedApi")
@Composable
fun LibraryOnboardingView(
    onClose: () -> Unit,
    onLibrary: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    systemUiController.isSystemBarsVisible = false
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        systemUiController.systemBarsBehavior =
            WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    DisposableEffect(lifecycleOwner) {
        onDispose {
            systemUiController.isSystemBarsVisible = true
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .clickable(false) {}
            .background(color = enforcedDarkest)
    ) {
        val (image, text, button, icon) = createRefs()
        val painter = rememberImagePainter(
            data = R.drawable.library_onboarding,
            builder = {
                size(Size.ORIGINAL)
                crossfade(true)
                error(R.drawable.library_onboarding_backup)
            }
        )
        Image(
            modifier = Modifier.constrainAs(image) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
                bottom.linkTo(button.top)
                height = Dimension.fillToConstraints
            },
            painter = painter,
            contentDescription = stringResource(id = R.string.explore_library),
            contentScale = ContentScale.FillHeight,
        )
        IconButton(
            modifier = Modifier
                .constrainAs(icon) {
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                }
                .padding(top = paddingLarge, end = paddingSmall),
            onClick = onClose,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = stringResource(id = R.string.close),
                tint = enforcedWhite
            )
        }
        Column(
            modifier = Modifier
                .widthIn(max = modalMaxWidth)
                .constrainAs(text) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(button.top)
                }
                .padding(vertical = paddingXLarge, horizontal = paddingMedium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.library_onboarding_title),
                color = enforcedWhite,
                textAlign = TextAlign.Center,
                style = onboardingTitle
            )
            Spacer(modifier = Modifier.height(paddingMedium))
            Text(
                text = stringResource(id = R.string.library_onboarding_description),
                color = enforcedBone,
                textAlign = TextAlign.Center,
                style = body1
            )
        }
        MainActionButton(
            modifier = Modifier
                .constrainAs(button) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(bottom = paddingXXGiant),
            text = stringResource(id = R.string.explore_library),
            textModifier = Modifier.padding(horizontal = paddingLarge, vertical = paddingSmall),
            textColor = enforcedDarkmo,
            onClick = onLibrary
        )
    }
}
