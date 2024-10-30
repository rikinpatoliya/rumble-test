package com.rumble.battles.profile.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.ContentDescriptionProfileMenuTag
import com.rumble.battles.ProfileMenuTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.NotificationIconView
import com.rumble.battles.commonViews.ProfileImageComponent
import com.rumble.battles.commonViews.ProfileImageComponentStyle
import com.rumble.battles.commonViews.RumbleLogoView
import com.rumble.battles.commonViews.RumbleProgressIndicatorWithDimmedBackground
import com.rumble.battles.commonViews.VideoUploadingIndicatorView
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.content.presentation.BottomSheetContent
import com.rumble.battles.content.presentation.ContentHandler
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.login.presentation.AuthHandler
import com.rumble.battles.login.presentation.AuthHandlerEvent
import com.rumble.battles.login.presentation.AuthPlaceholderScreen
import com.rumble.battles.navigation.RumbleScreens
import com.rumble.battles.profile.presentation.views.ProfileFollowingView
import com.rumble.battles.profile.presentation.views.ProfileItemView
import com.rumble.domain.channels.channeldetails.domain.domainmodel.ChannelDetailsEntity
import com.rumble.domain.profile.domainmodel.AppVersionVisibility
import com.rumble.domain.uploadmanager.dto.VideoUploadsIndicatorStatus
import com.rumble.network.queryHelpers.SubscriptionSource
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h6Light
import com.rumble.theme.borderXXSmall
import com.rumble.theme.followingHeaderIconSize
import com.rumble.theme.logoHeaderHeight
import com.rumble.theme.logoHeaderHeightTablets
import com.rumble.theme.logoHeight
import com.rumble.theme.logoHeightTablet
import com.rumble.theme.logoWidth
import com.rumble.theme.logoWidthTablet
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.clickableNoRipple
import com.rumble.utils.extension.conditional
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun ProfileScreen(
    activityHandler: RumbleActivityHandler,
    profileHandler: ProfileHandler,
    authHandler: AuthHandler,
    onProfileItemClicked: (navigationId: String) -> Unit,
    contentHandler: ContentHandler,
    onNavigateToRegistration: (String, String, String, String) -> Unit,
    onNavigateToAgeVerification: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onViewNotifications: () -> Unit,
) {
    val alertDialogState by profileHandler.alertDialogState.collectAsStateWithLifecycle()
    val uiState by profileHandler.uiState
    val screenSate by profileHandler.screenSate
    val appVersion by profileHandler.appVersionState.collectAsStateWithLifecycle()
    val activityHandlerState by activityHandler.activityHandlerState.collectAsStateWithLifecycle()
    val channelDetailsEntity: ChannelDetailsEntity? =
        (screenSate as? ProfileScreenState.LoggedIn)?.channelDetailsEntity
    val clipboard: ClipboardManager = LocalClipboardManager.current

    BackHandler {
        if (screenSate == ProfileScreenState.Loading) {
            Timber.d("BackHandler: Do nothing ignore swipe back and block any action while ProfileScreenState.Loading")
        } else {
            contentHandler.onNavigateHome()
        }
    }

    LaunchedEffect(Unit) {
        profileHandler.vmEvents.collect {
            when (it) {
                is ProfileScreenEvent.CopyVersionToClipboard -> {
                    clipboard.setText(AnnotatedString(it.version))
                    contentHandler.onShowSnackBar(
                        messageId = R.string.version_copied_message
                    )
                }

                ProfileScreenEvent.Error -> {
                    contentHandler.onError(null)
                }

                is ProfileScreenEvent.NavigateHome -> {
                    contentHandler.onNavigateHomeAfterSignedOut()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        authHandler.eventFlow.collectLatest { event ->
            when (event) {
                is AuthHandlerEvent.NavigateToAgeVerification -> {
                    onNavigateToAgeVerification()
                }

                else -> return@collectLatest
            }
        }
    }

    Box(
        modifier = Modifier
            .testTag(ProfileMenuTag)
            .semantics { contentDescription = ContentDescriptionProfileMenuTag }
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding())
    {
        if (uiState.isLoggedIn) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                val tablet = IsTablet()
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.onPrimary)
                        .height(if (tablet) logoHeaderHeightTablets else logoHeaderHeight)
                ) {
                    val (version, logo, notifications, settings, line) = createRefs()

                    RumbleLogoView(
                        modifier = Modifier
                            .constrainAs(logo) {
                                start.linkTo(parent.start, margin = paddingMedium)
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                            }
                            .size(
                                width = if (tablet) logoWidthTablet else logoWidth,
                                height = if (tablet) logoHeightTablet else logoHeight
                            ),
                    )

                    if (appVersion.visibility == AppVersionVisibility.Visible) {
                        Text(
                            modifier = Modifier
                                .constrainAs(version) {
                                    top.linkTo(parent.top, margin = paddingMedium)
                                    bottom.linkTo(parent.bottom, margin = paddingMedium)
                                    start.linkTo(logo.end, margin = paddingMedium)
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onDoubleTap = { profileHandler.onVersionClick() },
                                    )
                                },
                            text = appVersion.versionString,
                            style = h6Light
                        )
                    }

                    NotificationIconView(
                        modifier = Modifier
                            .constrainAs(notifications) {
                                end.linkTo(settings.start)
                                top.linkTo(logo.top)
                                bottom.linkTo(logo.bottom)
                            },
                        showDot = activityHandlerState.hasUnreadNotifications,
                        onClick = {
                            activityHandler.clearNotifications()
                            onViewNotifications()
                        }
                    )

                    IconButton(
                        modifier = Modifier
                            .constrainAs(settings) {
                                end.linkTo(parent.end)
                                top.linkTo(logo.top)
                                bottom.linkTo(logo.bottom)
                            },
                        onClick = onNavigateToSettings
                    ) {
                        Icon(
                            modifier = Modifier.size(followingHeaderIconSize),
                            painter = painterResource(id = R.drawable.ic_settings),
                            contentDescription = stringResource(id = R.string.settings),
                            tint = MaterialTheme.colors.primary
                        )
                    }

                    if (tablet) {
                        Divider(
                            Modifier
                                .height(borderXXSmall)
                                .fillMaxWidth()
                                .constrainAs(line) { bottom.linkTo(parent.bottom) },
                            color = MaterialTheme.colors.secondaryVariant
                        )
                    }
                }
                BoxWithConstraints {
                    val horizontalPadding = CalculatePaddingForTabletWidth(maxWidth = maxWidth)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .conditional(IsTablet()) {
                                padding(horizontal = horizontalPadding)
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(paddingXLarge))
                        ProfileImageComponent(
                            modifier = Modifier.clickableNoRipple { profileHandler.onProfileImageClick() },
                            profileImageComponentStyle = ProfileImageComponentStyle.CircleImageXLargeStyle(),
                            userName = uiState.userName,
                            userPicture = uiState.userPicture
                        )

                        Text(
                            modifier = Modifier.padding(
                                top = paddingMedium,
                                bottom = paddingMedium
                            ),
                            text = uiState.userName,
                            style = RumbleTypography.h1
                        )

                        ProfileFollowingView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                                .padding(
                                    start = paddingLarge,
                                    end = paddingLarge,
                                ),
                            rumbles = channelDetailsEntity?.rumbles ?: 0,
                            followers = channelDetailsEntity?.followers ?: 0,
                            following = channelDetailsEntity?.following ?: 0
                        )

                        Spacer(modifier = Modifier.height(paddingSmall))

                        if (uiState.isPremiumUser) {
                            ProfileItemView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = paddingSmall,
                                        horizontal = paddingLarge
                                    ),
                                iconId = R.drawable.ic_discover,
                                labelId = R.string.rumble_premium_plan,
                                onClick = {
                                    contentHandler.updateBottomSheetUiState(
                                        BottomSheetContent.PremiumOptions
                                    )
                                },
                                tint = rumbleGreen
                            )
                        } else {
                            ProfileItemView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = paddingSmall,
                                        horizontal = paddingLarge
                                    ),
                                iconId = R.drawable.ic_discover,
                                labelId = R.string.get_rumble_premium_plan,
                                onClick = {
                                    contentHandler.onShowSubscriptionOptions(videoId = null, source = SubscriptionSource.Profile)
                                }
                            )
                        }

                        ProfileItemView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = paddingSmall,
                                    horizontal = paddingLarge
                                ),
                            iconId = R.drawable.ic_nav_content,
                            labelId = R.string.your_videos,
                            onClick = { onProfileItemClicked(RumbleScreens.Videos.rootName) },
                            trailingIcon = {
                                if (uiState.userActiveUploadsIndicatorStatus != VideoUploadsIndicatorStatus.None)
                                    VideoUploadingIndicatorView(
                                        uiState.userActiveUploadsIndicatorStatus,
                                        uiState.userActiveUploadsProgress
                                    )
                            }
                        )

                        ProfileItemView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = paddingSmall,
                                    horizontal = paddingLarge
                                ),
                            iconId = R.drawable.ic_subscriptions,
                            labelId = R.string.following,
                            onClick = { onProfileItemClicked(RumbleScreens.Subscriptions.rootName) }
                        )

                        ProfileItemView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = paddingSmall,
                                    horizontal = paddingLarge
                                ),
                            iconId = R.drawable.ic_referrals,
                            labelId = R.string.referrals,
                            onClick = { onProfileItemClicked(RumbleScreens.Referrals.rootName) }
                        )

                        ProfileItemView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = paddingSmall,
                                    horizontal = paddingLarge
                                ),
                            iconId = R.drawable.ic_dollar,
                            labelId = R.string.earnings,
                            onClick = { onProfileItemClicked(RumbleScreens.EarningsScreen.rootName) }
                        )

                        Divider(
                            modifier = Modifier.padding(end = paddingLarge, top = paddingSmall),
                            startIndent = paddingLarge,
                            thickness = 1.dp,
                            color = MaterialTheme.colors.secondaryVariant
                        )

                        ProfileItemView(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = paddingLarge,
                                    horizontal = paddingLarge
                                ),
                            iconId = R.drawable.ic_sign_out,
                            labelId = R.string.sign_out,
                            onClick = profileHandler::onSignOut
                        )
                        BottomNavigationBarScreenSpacer()
                    }
                }
            }
        } else {
            AuthPlaceholderScreen(
                modifier = Modifier.fillMaxSize(),
                authHandler = authHandler,
                contentHandler = contentHandler,
                onNavigateToRegistration = onNavigateToRegistration,
                onEmailLogin = onNavigateToLogin,
                onSettings = onNavigateToSettings
            )
        }
    }

    if (alertDialogState.show) {
        if (alertDialogState.alertDialogReason is ProfileAlertDialogReason.ConfirmSignOut) {
            SignOutConfirmationDialog(
                signOut = profileHandler::onSignOutConfirmed,
                onDismiss = profileHandler::onDismissDialog
            )
        }
    }

    if (screenSate == ProfileScreenState.Loading) {
        RumbleProgressIndicatorWithDimmedBackground()
    }
}

@Composable
private fun SignOutConfirmationDialog(signOut: () -> Unit, onDismiss: () -> Unit) {
    RumbleAlertDialog(
        onDismissRequest = { onDismiss() },
        title = stringResource(id = R.string.sign_out),
        text = stringResource(id = R.string.sign_out_confirmation_message),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.cancel),
                action = onDismiss,
                withSpacer = true,
            ),
            DialogActionItem(
                text = stringResource(id = R.string.sign_out),
                action = signOut,
                dialogActionType = DialogActionType.Destructive,
            )
        )
    )
}