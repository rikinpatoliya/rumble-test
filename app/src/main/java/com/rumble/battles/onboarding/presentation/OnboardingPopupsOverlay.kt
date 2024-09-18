package com.rumble.battles.onboarding.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.FeedHintsTag
import com.rumble.battles.R
import com.rumble.domain.onboarding.domain.domainmodel.OnboardingPopupType
import com.rumble.theme.enforcedBlack
import com.rumble.theme.modalMaxWidth
import com.rumble.theme.radiusMedium
import com.rumble.theme.radiusXXXMedium
import com.rumble.utils.extension.clickableNoRipple

@Composable
fun OnboardingPopupsOverlay(
    handler: OnboardingHandler,
    list: List<OnboardingPopupType>
) {
    val popupsListIndex by handler.popupsListIndex.collectAsStateWithLifecycle()
    val discoverIconLocationState =
        handler.discoverIconLocationState.collectAsStateWithLifecycle()
    val searchIconLocationState =
        handler.searchIconLocationState.collectAsStateWithLifecycle()
    val followingIconLocationState =
        handler.followingIconLocationState.collectAsStateWithLifecycle()
    val libraryIconLocationState =
        handler.libraryIconLocationState.collectAsStateWithLifecycle()

    val overlayBackground = enforcedBlack.copy(alpha = 0.7f)
    val topHintIconRadius = radiusXXXMedium
    val bottomHintIconRadius = radiusXXXMedium + radiusMedium

    val displayPopup = list[popupsListIndex]

    Canvas(modifier = Modifier
        .testTag(FeedHintsTag)
        .fillMaxSize()
        .graphicsLayer {
            alpha = .99f
        }) {

        drawRect(overlayBackground)

        when (displayPopup) {
            OnboardingPopupType.SearchRumble -> {
                drawCircle(
                    Brush.radialGradient(
                        .85f to Color.Transparent,
                        1f to overlayBackground,
                        radius = topHintIconRadius.toPx(),
                        center = searchIconLocationState.value
                    ),
                    radius = topHintIconRadius.toPx(),
                    center = searchIconLocationState.value,
                    blendMode = BlendMode.Src
                )
            }

            OnboardingPopupType.DiscoverContent -> {
                drawCircle(
                    Brush.radialGradient(
                        .85f to Color.Transparent,
                        1f to overlayBackground,
                        radius = bottomHintIconRadius.toPx(),
                        center = discoverIconLocationState.value
                    ),
                    radius = bottomHintIconRadius.toPx(),
                    center = discoverIconLocationState.value,
                    blendMode = BlendMode.Src
                )
            }

            OnboardingPopupType.FollowingChannels -> {
                drawCircle(
                    Brush.radialGradient(
                        .85f to Color.Transparent,
                        1f to overlayBackground,
                        radius = topHintIconRadius.toPx(),
                        center = followingIconLocationState.value
                    ),
                    radius = topHintIconRadius.toPx(),
                    center = followingIconLocationState.value,
                    blendMode = BlendMode.Src
                )
            }

            OnboardingPopupType.YourLibrary -> {
                drawCircle(
                    Brush.radialGradient(
                        .85f to Color.Transparent,
                        1f to overlayBackground,
                        radius = bottomHintIconRadius.toPx(),
                        center = libraryIconLocationState.value
                    ),
                    radius = bottomHintIconRadius.toPx(),
                    center = libraryIconLocationState.value,
                    blendMode = BlendMode.Src
                )
            }
        }
    }

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .clickableNoRipple { /* Prevent clicks from propagating through overlay */ }) {
        val (dialogTop, dialogBottom) = createRefs()

        when (displayPopup) {
            OnboardingPopupType.SearchRumble, OnboardingPopupType.FollowingChannels -> {
                with(LocalDensity.current) {
                    OnboardingDialogView(
                        modifier = Modifier
                            .sizeIn(maxWidth = modalMaxWidth)
                            .constrainAs(dialogTop) {
                                top.linkTo(
                                    parent.top,
                                    margin = searchIconLocationState.value.y.toDp() + topHintIconRadius
                                )
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        onboardingPopupType = displayPopup,
                        mainActionText = getMainActionText(popupsListIndex, list),
                        stepText = getStepText(popupsListIndex, list),
                        showSkipAll = showSkipAll(popupsListIndex, list),
                        onSkipAll = { handler.onSkipAll(list) },
                        onNext = { handler.onNext(displayPopup, popupsListIndex, list) },
                        showBackIndicator = showBackIndicator(popupsListIndex, list),
                        onBack = { handler.onBack(popupsListIndex) },
                    )
                }
            }

            OnboardingPopupType.DiscoverContent, OnboardingPopupType.YourLibrary -> {
                with(LocalDensity.current) {
                    OnboardingDialogView(
                        modifier = Modifier
                            .sizeIn(maxWidth = modalMaxWidth)
                            .constrainAs(dialogBottom) {
                                bottom.linkTo(
                                    parent.top,
                                    margin = -discoverIconLocationState.value.y.toDp() + bottomHintIconRadius
                                )
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        onboardingPopupType = displayPopup,
                        mainActionText = getMainActionText(popupsListIndex, list),
                        stepText = getStepText(popupsListIndex, list),
                        showSkipAll = showSkipAll(popupsListIndex, list),
                        onSkipAll = { handler.onSkipAll(list) },
                        onNext = { handler.onNext(displayPopup, popupsListIndex, list) },
                        showBackIndicator = showBackIndicator(popupsListIndex, list),
                        onBack = { handler.onBack(popupsListIndex) },
                    )
                }
            }
        }
    }
}

@Composable
private fun showSkipAll(
    popupsListIndex: Int,
    list: List<OnboardingPopupType>
) = list.size > 1 && list.lastIndex != popupsListIndex

@Composable
private fun showBackIndicator(
    popupsListIndex: Int,
    list: List<OnboardingPopupType>
) = list.size > 1 && popupsListIndex != 0


@Composable
private fun getStepText(popupsListIndex: Int, list: List<OnboardingPopupType>): String? {
    return if (list.size == 1) {
        null
    } else {
        "${popupsListIndex + 1} ${stringResource(id = R.string.of)} ${list.size}"
    }
}

@Composable
private fun getMainActionText(popupsListIndex: Int, list: List<OnboardingPopupType>) =
    if (popupsListIndex == list.lastIndex)
        stringResource(id = R.string.okay)
    else
        stringResource(id = R.string.next)
