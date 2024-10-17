package com.rumble.battles.login.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.R
import com.rumble.battles.commonViews.DarkModeBackground
import com.rumble.battles.commonViews.DarkSystemNavigationBar
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.RumbleAuthTopAppBar
import com.rumble.battles.commonViews.RumbleInputSelectorFieldView
import com.rumble.battles.commonViews.RumbleProgressIndicatorWithDimmedBackground
import com.rumble.battles.commonViews.RumbleWheelDatePicker
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.AnnotatedTextAction
import com.rumble.theme.RumbleTypography
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedGray900
import com.rumble.theme.enforcedWhite
import com.rumble.theme.loginContentWidthTablet
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingSmall
import com.rumble.theme.paddingXLarge
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.BIRTHDAY_DATE_PATTERN
import com.rumble.utils.RumbleConstants.TAG_EMAIL
import com.rumble.utils.RumbleConstants.TAG_URL
import com.rumble.utils.errors.InputValidationError
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.convertToDate
import com.rumble.utils.extension.toUtcLocalDate
import com.rumble.utils.extension.toUtcLong

private const val TAG = "AgeVerificationScreen"

@Composable
fun AgeVerificationScreen(
    darkMode: Boolean = true,
    ageVerificationHandler: AgeVerificationHandler,
    activityHandler: RumbleActivityHandler,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToWebView: (String) -> Unit
) {
    val state by ageVerificationHandler.uiState.collectAsStateWithLifecycle()
    val alertDialogState by ageVerificationHandler.alertDialogState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    BackHandler {
        if (showDatePicker) showDatePicker = false
        else activityHandler.closeApp()
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val observer = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_PAUSE) {
            ageVerificationHandler.saveAgeNotVerifiedState()
        }
    }
    LaunchedEffect(key1 = context) {
        lifecycle.addObserver(observer)
        ageVerificationHandler.vmEvents.collect { event ->
            when (event) {
                is AgeVerificationScreenVmEvent.Error -> {
                    activityHandler.showSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is AgeVerificationScreenVmEvent.NavigateToHomeScreen -> {
                    lifecycle.removeObserver(observer)
                    onNavigateToHomeScreen()
                }

                is AgeVerificationScreenVmEvent.NavigateBack -> {
                    lifecycle.removeObserver(observer)
                    onNavigateBack()
                }

                is AgeVerificationScreenVmEvent.NavigateToWebView -> {
                    onNavigateToWebView(event.url)
                }
            }
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    TransparentStatusBar(withOnDispose = false)
    DarkSystemNavigationBar()

    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        if (darkMode) DarkModeBackground(Modifier.fillMaxSize())

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RumbleAuthTopAppBar(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .padding(top = paddingLarge)
            )

            Column(
                modifier = Modifier
                    .conditional(IsTablet()) {
                        widthIn(max = loginContentWidthTablet)
                    }
                    .conditional(IsTablet().not()) {
                        padding(horizontal = paddingMedium)
                    }
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(paddingXLarge))

                AgeVerificationTitle()

                Spacer(Modifier.height(paddingXLarge))

                RumbleInputSelectorFieldView(
                    label = stringResource(id = R.string.birthday).uppercase(),
                    labelColor = enforcedWhite,
                    backgroundColor = enforcedGray900,
                    textColor = enforcedWhite,
                    errorMessageColor = enforcedBone,
                    extraLabel = stringResource(id = R.string.why_we_ask_for_birth),
                    extraLabelClicked = { ageVerificationHandler.onWhyWeAskBirthdayClicked() },
                    value = if (state.userProfileEntity.birthday == null) "" else state.userProfileEntity.birthday?.toUtcLong()
                        ?.convertToDate(
                            pattern = BIRTHDAY_DATE_PATTERN, useUtc = true
                        ) ?: "",
                    hasError = state.birthdayError.first,
                    errorMessage = when (state.birthdayError.second) {
                        InputValidationError.Empty -> stringResource(id = R.string.birthday_empty_error_message)
                        InputValidationError.MinCharacters -> stringResource(
                            id = R.string.birthday_at_least_13_error_message
                        )

                        else -> ""
                    }
                ) {
                    showDatePicker = showDatePicker.not()
                    if (showDatePicker) focusManager.clearFocus()
                }

                Spacer(Modifier.height(paddingXLarge))

                MainActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    textModifier = Modifier.padding(vertical = paddingSmall),
                    text = stringResource(id = R.string.age_verification_submit),
                    textColor = enforcedDarkmo,
                    onClick = {
                        focusManager.clearFocus()
                        ageVerificationHandler.onUpdateBirthday()
                    }
                )
            }
        }

        if (showDatePicker) {
            RumbleWheelDatePicker(
                initialValue = state.userProfileEntity.birthday?.toUtcLong() ?: 0L,
                onChanged = { ageVerificationHandler.onBirthdayChanged(it.toUtcLocalDate()) },
                onDismissRequest = { showDatePicker = false }
            )
        }
    }
    if (state.loading) {
        RumbleProgressIndicatorWithDimmedBackground()
    }
    if (alertDialogState.show) {
        if (alertDialogState.alertDialogReason is AgeVerificationScreenAlertDialogReason.WhyWeAskBirthdayDialogReason) {
            RumbleAlertDialog(
                onDismissRequest = { ageVerificationHandler.onDismissDialog() },
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(id = R.string.ok),
                        action = { ageVerificationHandler.onDismissDialog() },
                        dialogActionType = DialogActionType.Neutral,
                    )
                ),
                annotatedTextWithActions = buildWhyWeAskStringWithActions(ageVerificationHandler),
                onAnnotatedTextClicked = ageVerificationHandler::onAnnotatedTextClicked,
            )
        }
    }
}

@Composable
private fun buildWhyWeAskStringWithActions(
    ageVerificationHandler: AgeVerificationHandler
): AnnotatedStringWithActionsList {
    val actionList = mutableListOf<AnnotatedTextAction>()
    val text = buildAnnotatedString {
        append(stringResource(id = R.string.date_of_birth_dialog_text_part_1))
        append(" ")
        withStyle(style = SpanStyle(color = rumbleGreen)) {
            actionList.add(AnnotatedTextAction(TAG_URL) { uri ->
                ageVerificationHandler.onOpenUri(
                    TAG,
                    uri
                )
            })
            val startIndexTerms = this.length
            append(stringResource(id = R.string.privacy_policy))
            val endIndexTerms = this.length
            addStringAnnotation(
                tag = TAG_URL,
                annotation = stringResource(id = R.string.rumble_privacy_policy_url),
                start = startIndexTerms,
                end = endIndexTerms
            )
        }
        append(" ")
        append(stringResource(id = R.string.date_of_birth_dialog_text_part_2))
        append(" ")
        withStyle(style = SpanStyle(color = rumbleGreen)) {
            actionList.add(AnnotatedTextAction(TAG_EMAIL) { email ->
                ageVerificationHandler.onSendEmail(email)
            })
            val startIndexPrivacy = this.length
            append(stringResource(id = R.string.support_email))
            val endIndexPrivacy = this.length
            addStringAnnotation(
                tag = TAG_EMAIL,
                annotation = stringResource(id = R.string.support_email),
                start = startIndexPrivacy,
                end = endIndexPrivacy
            )
        }
        append(".")
    }
    return AnnotatedStringWithActionsList(text, actionList)
}

@Composable
fun AgeVerificationTitle() {
    Text(
        text = stringResource(id = R.string.age_verification_title),
        style = h3,
        color = enforcedWhite
    )

    Spacer(Modifier.height(paddingXXXSmall))

    Text(
        text = stringResource(id = R.string.age_verification_subtitle),
        style = RumbleTypography.smallBody,
        color = enforcedWhite,
        textAlign = TextAlign.Center
    )
}