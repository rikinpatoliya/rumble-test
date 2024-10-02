package com.rumble.battles.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.AndroidIdSettingsTag
import com.rumble.battles.AppInfoSectionTag
import com.rumble.battles.BackgroundPlaySectionTag
import com.rumble.battles.DebugSectionTag
import com.rumble.battles.PlaybackInFeedsTag
import com.rumble.battles.R
import com.rumble.battles.UserDetailsSectionTag
import com.rumble.battles.commonViews.BottomNavigationBar
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.CheckMarkItem
import com.rumble.battles.commonViews.RowItemWithArrowView
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.ToggleRowView
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.navigation.RumbleScreens
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.settings.domain.domainmodel.BackgroundPlay
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.domainmodel.PlaybackInFeedsMode
import com.rumble.domain.settings.domain.domainmodel.UploadQuality
import com.rumble.theme.RumbleTypography
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXSmall
import com.rumble.theme.settingItemRowHeight

private const val playbackSettingsIndex = 1

@Composable
fun SettingsScreen(
    settingsHandler: SettingsHandler,
    onBackClick: () -> Unit,
    onNavigate: (id: String) -> Unit,
) {
    val state by settingsHandler.uiState.collectAsStateWithLifecycle()
    val loginType by settingsHandler.loginTypeFlow.collectAsStateWithLifecycle(
        initialValue = LoginType.UNKNOWN
    )
    val backgroundPlay by settingsHandler.backgroundPlay.collectAsStateWithLifecycle(
        initialValue = BackgroundPlay.PICTURE_IN_PICTURE
    )
    val uploadOverWifi by settingsHandler.uploadOverWifi.collectAsStateWithLifecycle(
        initialValue = false
    )
    val uploadQuality by settingsHandler.uploadQuality.collectAsStateWithLifecycle(
        initialValue = UploadQuality.defaultUploadQuality
    )
    val playbackInFeedsMode by settingsHandler.playbackInFeedsMode.collectAsStateWithLifecycle(
        initialValue = PlaybackInFeedsMode.ALWAYS_ON
    )
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    val clipboard: ClipboardManager = LocalClipboardManager.current

    LaunchedEffect(key1 = context) {
        settingsHandler.vmEvents.collect { event ->
            when (event) {
                is SettingsScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is SettingsScreenVmEvent.AccountUnlinkSuccess -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = String.format(
                            context.getString(R.string.unlink_account_success_message),
                            context.getString(loginType.stringId).lowercase()
                        )
                    )
                }

                is SettingsScreenVmEvent.ScrollToPlaybackSettings -> {
                    listState.animateScrollToItem(playbackSettingsIndex)
                }

                is SettingsScreenVmEvent.CopyVersionToClipboard -> {
                    clipboard.setText(AnnotatedString(event.version))
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.version_copied_message)
                    )
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .testTag(AndroidIdSettingsTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .systemBarsPadding()
    ) {
        val (topBar, content) = createRefs()
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.settings),
            modifier = Modifier
                .constrainAs(topBar) { top.linkTo(parent.top) }
                .fillMaxWidth(),
            onBackClick = onBackClick,
        )

        BoxWithConstraints(modifier = Modifier
            .constrainAs(content) {
                top.linkTo(topBar.bottom)
                bottom.linkTo(parent.bottom)
                height = Dimension.fillToConstraints
            }) {

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(
                    horizontal = CalculatePaddingForTabletWidth(
                        maxWidth = maxWidth
                    )
                )
            ) {
                item {
                    Spacer(
                        Modifier
                            .height(paddingLarge)
                    )
                    if (state.userLoggedIn) {
                        UserDetailsSection(
                            modifier = Modifier.testTag(UserDetailsSectionTag),
                            loginType = loginType,
                            onNavigate = onNavigate,
                        )
                        Spacer(
                            Modifier
                                .height(paddingLarge)
                        )
                    }
                }
                item {
                    AppAppearanceSection(
                        settingsHandler = settingsHandler,
                        colorMode = state.colorMode
                    )
                    Spacer(
                        Modifier
                            .height(paddingLarge)
                    )
                }
                item {
                    BackgroundPlaySection(
                        modifier = Modifier.testTag(BackgroundPlaySectionTag),
                        settingsHandler = settingsHandler,
                        backgroundPlay = backgroundPlay
                    )
                    Spacer(
                        Modifier
                            .height(paddingLarge)
                    )
                }
                item {
                    PlaybackInFeedsSection(
                        modifier = Modifier.testTag(PlaybackInFeedsTag),
                        settingsHandler = settingsHandler,
                        playbackInFeedsMode = playbackInFeedsMode
                    )
                }
                item {
                    Spacer(
                        Modifier
                            .height(paddingLarge)
                    )
                    AutoplaySection(
                        settingsHandler = settingsHandler,
                        autoplayEnabled = state.autoplayEnabled
                    )
                }
                item {
                    Spacer(
                        Modifier
                            .height(paddingLarge)
                    )
                    UploadsSection(
                        settingsHandler,
                        uploadOverWifi,
                        uploadQuality,
                        onNavigate
                    )
                    Spacer(
                        Modifier
                            .height(paddingLarge)
                    )
                    if (state.debugState.canUseSubdomain
                        || state.debugState.rumbleSubdomain.canResetSubdomain
                        || (state.debugState.authProviderEntity != null && state.debugState.authProviderEntity?.canUnlink == true)
                        || state.debugState.canSubmitLogs
                        || state.debugState.canUseAdsDebugMode
                    ) {
                        DebugSection(
                            modifier = Modifier.testTag(DebugSectionTag),
                            settingsHandler = settingsHandler,
                            debugState = state.debugState,
                            onNavigate = onNavigate,
                        )
                        Spacer(
                            Modifier
                                .height(paddingLarge)
                        )
                    }
                    AppInfoSection(
                        modifier = Modifier.testTag(AppInfoSectionTag),
                        appVersion = state.appVersion,
                        onNavigate = onNavigate,
                        onSendFeedback = settingsHandler::onSendFeedback,
                        onVersionClick = settingsHandler::onVersionClick
                    )
                }
                item {
                    BottomNavigationBarScreenSpacer()
                }
            }
        }
    }
    if (state.loading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    if (state.alertDialogState.show) {
        if (state.alertDialogState.alertDialogReason is SettingsAlertDialogReason.UnlinkConfirmationDialog) {
            UnlinkAccountConfirmationDialog(
                settingsHandler = settingsHandler,
                loginType = (state.alertDialogState.alertDialogReason as SettingsAlertDialogReason.UnlinkConfirmationDialog).loginType
            )
        } else if (state.alertDialogState.alertDialogReason is SettingsAlertDialogReason.ChangeSubdomainConfirmationDialog) {
            ChangeSubdomainConfirmationDialog(
                onDismissDialog = { settingsHandler.onDismissDialog() }
            )
        }
    }
    RumbleSnackbarHost(snackBarHostState)
}

@Composable
private fun AppInfoSection(
    modifier: Modifier = Modifier,
    appVersion: String,
    onNavigate: (id: String) -> Unit,
    onSendFeedback: () -> Unit,
    onVersionClick: () -> Unit,
) {
    Text(
        modifier = modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.app_info),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    RowItemWithArrowView(
        modifier = Modifier
            .clickable { onNavigate(RumbleScreens.Credits.rootName) },
        text = stringResource(id = R.string.credits),
        addSeparator = true,
    )
    RowItemWithArrowView(
        modifier = Modifier
            .clickable { onSendFeedback() },
        text = stringResource(id = R.string.send_feedback),
        addSeparator = true,
    )
    Row(
        modifier = Modifier
            .height(settingItemRowHeight)
            .padding(
                start = paddingMedium,
                end = paddingMedium
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onVersionClick() },
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${stringResource(id = R.string.rumble_app_version)}$appVersion",
            style = RumbleTypography.body1,
        )
    }
}

@Composable
private fun UserDetailsSection(
    modifier: Modifier = Modifier,
    loginType: LoginType,
    onNavigate: (id: String) -> Unit,
) {
    Text(
        modifier = modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.user_details),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    RowItemWithArrowView(
        modifier = Modifier
            .clickable { onNavigate(RumbleScreens.EditProfile.rootName) },
        text = stringResource(id = R.string.edit_profile),
        addSeparator = true,
    )
    RowItemWithArrowView(
        modifier = Modifier
            .clickable { onNavigate(RumbleScreens.NotificationSettings.rootName) },
        text = stringResource(id = R.string.notification_settings),
        addSeparator = true,
    )
    if (loginType == LoginType.RUMBLE) {
        RowItemWithArrowView(
            modifier = Modifier
                .clickable { onNavigate(RumbleScreens.ChangeEmail.rootName) },
            text = stringResource(id = R.string.change_email),
            addSeparator = true,
        )
        RowItemWithArrowView(
            modifier = Modifier
                .clickable { onNavigate(RumbleScreens.ChangePassword.rootName) },
            text = stringResource(id = R.string.change_password),
            addSeparator = true,
        )
    }
    RowItemWithArrowView(
        modifier = Modifier
            .clickable { onNavigate(RumbleScreens.CloseAccount.rootName) },
        text = stringResource(id = R.string.close_account),
        addSeparator = true,
    )
}

@Composable
private fun DebugSection(
    modifier: Modifier = Modifier,
    settingsHandler: SettingsHandler,
    debugState: DebugUIState,
    onNavigate: (id: String) -> Unit,
) {
    val disableAds by settingsHandler.disableAdsFlow.collectAsStateWithLifecycle(initialValue = false)
    val forceAds by settingsHandler.forceAdsFlow.collectAsStateWithLifecycle(initialValue = false)
    val playDebugAd by settingsHandler.playDebugAdFlow.collectAsStateWithLifecycle(initialValue = false)

    Text(
        modifier = modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.debug),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    if (debugState.canUseSubdomain) {
        RowItemWithArrowView(
            modifier = Modifier
                .clickable { onNavigate(RumbleScreens.ChangeSubdomain.rootName) },
            text = stringResource(id = R.string.subdomain),
            label = debugState.rumbleSubdomain.appSubdomain
                ?: debugState.rumbleSubdomain.environmentSubdomain,
            addSeparator = true,
        )
    }
    if (debugState.rumbleSubdomain.canResetSubdomain) {
        Column {
            Row(
                modifier = Modifier
                    .height(settingItemRowHeight)
                    .fillMaxWidth()
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium
                    )
                    .clickable { settingsHandler.onResetSubdomain() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.reset_subdomain),
                    style = RumbleTypography.body1,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingMedium),
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
    if (debugState.authProviderEntity != null && debugState.authProviderEntity.canUnlink) {
        Column {
            Row(
                modifier = Modifier
                    .height(settingItemRowHeight)
                    .fillMaxWidth()
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium
                    )
                    .clickable { settingsHandler.onAskUnlinkConfirmation(debugState.authProviderEntity.loginType) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format(
                        stringResource(id = R.string.unlink_account),
                        stringResource(id = debugState.authProviderEntity.loginType.stringId).lowercase()
                    ),
                    style = RumbleTypography.body1,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingMedium),
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
    if (debugState.canSubmitLogs) {
        Column {
            val shareTitle = stringResource(id = R.string.share_logs)
            val localConfig = LocalConfiguration.current
            val localDensity = LocalDensity.current
            Row(
                modifier = Modifier
                    .height(settingItemRowHeight)
                    .fillMaxWidth()
                    .padding(
                        start = paddingMedium,
                        end = paddingMedium
                    )
                    .clickable {
                        settingsHandler.onShareLogs(
                            title = shareTitle,
                            widthDp = localConfig.screenWidthDp,
                            heightDp = localConfig.screenHeightDp,
                            width = with(localDensity) { localConfig.screenWidthDp.dp.roundToPx() },
                            height = with(localDensity) { localConfig.screenHeightDp.dp.roundToPx() },
                        )
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.share_logs),
                    style = RumbleTypography.body1,
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = paddingMedium),
                color = MaterialTheme.colors.secondaryVariant
            )
        }
    }
    if (debugState.canUseAdsDebugMode) {
        ToggleRowView(
            text = stringResource(id = R.string.disable_ads),
            textStyle = RumbleTypography.body1,
            checked = disableAds,
            addSeparator = true,
            onCheckedChange = settingsHandler::onDisableAds
        )

        ToggleRowView(
            text = stringResource(id = R.string.force_ads),
            textStyle = RumbleTypography.body1,
            checked = forceAds,
            addSeparator = true,
            onCheckedChange = settingsHandler::onForceAds,
            enabled = disableAds.not(),
        )

        ToggleRowView(
            text = stringResource(id = R.string.always_play_debug_ad),
            textStyle = RumbleTypography.body1,
            checked = playDebugAd,
            addSeparator = true,
            onCheckedChange = settingsHandler::onPlayDebugAd,
            enabled = disableAds.not(),
        )
    }
}

@Composable
private fun UploadsSection(
    settingsHandler: SettingsHandler,
    uploadOverWifi: Boolean,
    uploadQuality: UploadQuality,
    onNavigate: (id: String) -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.uploads),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    ToggleRowView(
        text = stringResource(id = R.string.upload_over_wifi_only),
        textStyle = RumbleTypography.body1,
        checked = uploadOverWifi,
        addSeparator = true,
        onCheckedChange = { settingsHandler.onUpdateUploadOverWifi(it) }
    )
    RowItemWithArrowView(
        modifier = Modifier
            .clickable { onNavigate(RumbleScreens.UploadQuality.rootName) },
        text = stringResource(id = R.string.upload_quality),
        label = stringResource(id = uploadQuality.titleId),
        addSeparator = true,
    )
}

@Composable
private fun BackgroundPlaySection(
    modifier: Modifier = Modifier,
    settingsHandler: SettingsHandler,
    backgroundPlay: BackgroundPlay
) {
    Text(
        modifier = modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.background_play),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    CheckMarkItem(
        title = stringResource(id = R.string.picture_in_picture),
        selected = backgroundPlay == BackgroundPlay.PICTURE_IN_PICTURE,
        addSeparator = true,
        onClick = { settingsHandler.onUpdateBackgroundPlay(BackgroundPlay.PICTURE_IN_PICTURE) }
    )
    CheckMarkItem(
        title = stringResource(id = R.string.sound_only),
        selected = backgroundPlay == BackgroundPlay.SOUND,
        addSeparator = true,
        onClick = { settingsHandler.onUpdateBackgroundPlay(BackgroundPlay.SOUND) }
    )
    CheckMarkItem(
        title = stringResource(id = R.string.off),
        selected = backgroundPlay == BackgroundPlay.OFF,
        addSeparator = true,
        onClick = { settingsHandler.onUpdateBackgroundPlay(BackgroundPlay.OFF) }
    )
}

@Composable
private fun UnlinkAccountConfirmationDialog(
    settingsHandler: SettingsHandler,
    loginType: LoginType
) {
    RumbleAlertDialog(
        onDismissRequest = { settingsHandler.onDismissDialog() },
        title = null,
        text = String.format(
            stringResource(id = R.string.unlink_account_confirmation_question),
            stringResource(id = loginType.stringId).lowercase()
        ),
        actionItems = listOf(
            DialogActionItem(
                text = stringResource(id = R.string.close),
                withSpacer = true,
                action = { settingsHandler.onDismissDialog() }
            ),
            DialogActionItem(
                text = stringResource(id = R.string.unlink),
                action = { settingsHandler.onUnlinkAccount(loginType) },
                dialogActionType = DialogActionType.Destructive,
            ),
        )
    )
}

@Composable
private fun PlaybackInFeedsSection(
    modifier: Modifier = Modifier,
    settingsHandler: SettingsHandler,
    playbackInFeedsMode: PlaybackInFeedsMode
) {
    Text(
        modifier = modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.playback_in_feeds),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    CheckMarkItem(
        title = stringResource(id = R.string.playback_always_on),
        selected = playbackInFeedsMode == PlaybackInFeedsMode.ALWAYS_ON,
        addSeparator = true,
        onClick = { settingsHandler.onUpdatePlaybackInFeed(PlaybackInFeedsMode.ALWAYS_ON) }
    )
    CheckMarkItem(
        title = stringResource(id = R.string.playback_wifi_only),
        selected = playbackInFeedsMode == PlaybackInFeedsMode.WIFI_ONLY,
        addSeparator = true,
        onClick = { settingsHandler.onUpdatePlaybackInFeed(PlaybackInFeedsMode.WIFI_ONLY) }
    )
    CheckMarkItem(
        title = stringResource(id = R.string.off),
        selected = playbackInFeedsMode == PlaybackInFeedsMode.OFF,
        addSeparator = true,
        onClick = { settingsHandler.onUpdatePlaybackInFeed(PlaybackInFeedsMode.OFF) }
    )
}

@Composable
private fun AppAppearanceSection(
    modifier: Modifier = Modifier,
    settingsHandler: SettingsHandler,
    colorMode: ColorMode,
) {
    Text(
        modifier = modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.app_appearance),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    CheckMarkItem(
        title = stringResource(id = R.string.light_mode),
        selected = colorMode == ColorMode.LIGHT_MODE,
        addSeparator = true,
        onClick = { settingsHandler.onChangeColorMode(ColorMode.LIGHT_MODE) }
    )
    CheckMarkItem(
        title = stringResource(id = R.string.dark_mode),
        selected = colorMode == ColorMode.DARK_MODE,
        addSeparator = true,
        onClick = { settingsHandler.onChangeColorMode(ColorMode.DARK_MODE) }
    )
    CheckMarkItem(
        title = stringResource(id = R.string.system_default),
        selected = colorMode == ColorMode.SYSTEM_DEFAULT,
        addSeparator = true,
        onClick = { settingsHandler.onChangeColorMode(ColorMode.SYSTEM_DEFAULT) }
    )
}

@Composable
private fun AutoplaySection(
    modifier: Modifier = Modifier,
    settingsHandler: SettingsHandler,
    autoplayEnabled: Boolean,
) {
    Text(
        modifier = modifier
            .padding(start = paddingMedium),
        text = stringResource(id = R.string.autoplay),
        style = RumbleTypography.h1
    )
    Spacer(
        Modifier
            .height(paddingXSmall)
    )
    ToggleRowView(
        text = stringResource(id = R.string.autoplay_next_video),
        textStyle = RumbleTypography.body1,
        checked = autoplayEnabled,
        addSeparator = true,
        onCheckedChange = { settingsHandler.onAutoplayOn(it) }
    )
}
