package com.rumble.battles.login.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.AuthSignUp
import com.rumble.battles.AuthUsername
import com.rumble.battles.R
import com.rumble.battles.commonViews.DarkModeBackground
import com.rumble.battles.commonViews.DarkSystemNavigationBar
import com.rumble.battles.commonViews.ErrorMessageView
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.MenuSelectionItem
import com.rumble.battles.commonViews.PasswordView
import com.rumble.battles.commonViews.RumbleAuthTopAppBar
import com.rumble.battles.commonViews.RumbleDropDownMenu
import com.rumble.battles.commonViews.RumbleInputFieldView
import com.rumble.battles.commonViews.RumbleInputSelectorFieldView
import com.rumble.battles.commonViews.RumbleProgressIndicatorWithDimmedBackground
import com.rumble.battles.commonViews.RumbleWheelDatePicker
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.commonViews.dialogs.DialogActionItem
import com.rumble.battles.commonViews.dialogs.DialogActionType
import com.rumble.battles.commonViews.dialogs.RumbleAlertDialog
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.common.domain.usecase.AnnotatedStringWithActionsList
import com.rumble.domain.common.domain.usecase.AnnotatedTextAction
import com.rumble.domain.profile.domainmodel.Gender
import com.rumble.theme.RumbleTheme
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
import com.rumble.theme.paddingXSmall
import com.rumble.theme.paddingXXXSmall
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.BIRTHDAY_DATE_PATTERN
import com.rumble.utils.RumbleConstants.TAG_EMAIL
import com.rumble.utils.RumbleConstants.TAG_URL
import com.rumble.utils.errors.InputValidationError
import com.rumble.utils.extension.conditional
import com.rumble.utils.extension.convertToDate

private const val TAG = "RegisterScreen"

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegisterScreen(
    darkMode: Boolean = true,
    registerHandler: RegisterHandler,
    activityHandler: RumbleActivityHandler,
    onNavigateToHomeScreen: () -> Unit,
    onNavigateToAgeVerification: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToWebView: (String) -> Unit,
) {
    val state by registerHandler.uiState.collectAsStateWithLifecycle()
    val alertDialogState by registerHandler.alertDialogState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    var showDatePicker by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    BackHandler {
        if (showDatePicker) showDatePicker = false
        else onNavigateBack()
    }

    LaunchedEffect(key1 = context) {
        registerHandler.vmEvents.collect { event ->
            when (event) {
                is RegistrationScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is RegistrationScreenVmEvent.NavigateToHomeScreen -> {
                    onNavigateToHomeScreen()
                }

                is RegistrationScreenVmEvent.NavigateToAgeVerification -> {
                    onNavigateToAgeVerification()
                }

                is RegistrationScreenVmEvent.NavigateToWebView -> {
                    onNavigateToWebView(event.url)
                }
            }
        }
    }

    TransparentStatusBar(withOnDispose = false)
    DarkSystemNavigationBar()

    Box(
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .navigationBarsPadding()
            .semantics { testTagsAsResourceId = true }
            .testTag(if (state.ssoRegistration) AuthUsername else AuthSignUp)
    ) {
        if (darkMode) DarkModeBackground(Modifier.fillMaxSize())

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            RumbleAuthTopAppBar(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .padding(top = paddingLarge),
                onBackClick = onNavigateBack,
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
                Spacer(
                    Modifier
                        .height(paddingXLarge)
                )
                if (state.ssoRegistration)
                    UserNameSelectionDescription()
                else
                    RegistrationDescription(
                        loginAction = onNavigateBack
                    )
                Spacer(
                    Modifier
                        .height(paddingXLarge)
                )
                RumbleInputFieldView(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) showDatePicker = false
                    },
                    label = stringResource(id = R.string.username).uppercase(),
                    labelColor = enforcedWhite,
                    backgroundColor = enforcedGray900,
                    textColor = enforcedWhite,
                    cursorColor = enforcedWhite,
                    iconTintColor = enforcedWhite,
                    errorMessageColor = enforcedBone,
                    initialValue = state.userRegistrationEntity.userName,
                    onValueChange = registerHandler::onUserNameChanged,
                    hasError = state.usernameError.first,
                    errorMessage = when (state.usernameError.second) {
                        InputValidationError.NotLetterOrDigit -> stringResource(id = R.string.username_must_begin_with_letter_or_number)
                        InputValidationError.NotLetterOrDigitOrUnderscore -> stringResource(
                            id = R.string.username_can_only_contain
                        )

                        InputValidationError.MinCharacters -> stringResource(id = R.string.username_at_least_3_characters)
                        else -> ""
                    }
                )

                Spacer(
                    Modifier
                        .height(paddingMedium)
                )

                RumbleInputFieldView(
                    modifier = Modifier.onFocusChanged {
                        if (it.isFocused) showDatePicker = false
                    },
                    label = stringResource(id = R.string.email_address).uppercase(),
                    labelColor = enforcedWhite,
                    backgroundColor = enforcedGray900,
                    textColor = enforcedWhite,
                    cursorColor = enforcedWhite,
                    iconTintColor = enforcedWhite,
                    errorMessageColor = enforcedBone,
                    initialValue = state.userRegistrationEntity.email,
                    onValueChange = registerHandler::onEmailChanged,
                    hasError = state.emailError,
                    errorMessage = stringResource(id = R.string.enter_valid_email)
                )

                if (!state.ssoRegistration) {
                    Spacer(
                        Modifier
                            .height(paddingMedium)
                    )
                    PasswordView(
                        modifier = Modifier
                            .onFocusChanged {
                                if (it.isFocused) showDatePicker = false
                            },
                        label = stringResource(id = R.string.password_label),
                        labelColor = enforcedWhite,
                        backgroundColor = enforcedGray900,
                        textColor = enforcedWhite,
                        cursorColor = enforcedWhite,
                        iconTintColor = enforcedWhite,
                        errorMessageColor = enforcedBone,
                        initialValue = state.userRegistrationEntity.password,
                        onValueChange = registerHandler::onPasswordChanged,
                        hasError = state.passwordError,
                        errorMessage = stringResource(id = R.string.password_at_least_8_characters)
                    )
                }

                Spacer(
                    Modifier
                        .height(paddingMedium)
                )

                RumbleInputSelectorFieldView(
                    label = stringResource(id = R.string.birthday).uppercase(),
                    labelColor = enforcedWhite,
                    backgroundColor = enforcedGray900,
                    textColor = enforcedWhite,
                    errorMessageColor = enforcedBone,
                    extraLabel = stringResource(id = R.string.why_we_ask_for_birth),
                    extraLabelClicked = { registerHandler.onWhyWeAskBirthdayClicked() },
                    value = if (state.userRegistrationEntity.birthday == 0L) "" else state.userRegistrationEntity.birthday.convertToDate(
                        pattern = BIRTHDAY_DATE_PATTERN, useUtc = true
                    ),
                    hasError = state.birthdayError.first,
                    errorMessage = when (state.birthdayError.second) {
                        InputValidationError.Empty -> stringResource(id = R.string.birthday_empty_error_message)
                        InputValidationError.MinCharacters -> stringResource(
                            id = R.string.birthday_at_least_13_error_message
                        )

                        else -> ""
                    }
                ) {
                    if (!showDatePicker) {
                        showDatePicker = true
                        focusManager.clearFocus()
                    }
                }

                Spacer(
                    Modifier
                        .height(paddingMedium)
                )

                RumbleDropDownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    placeHolder = stringResource(id = R.string.select_gender),
                    label = stringResource(id = R.string.gender),
                    backgroundColor = enforcedGray900,
                    textColor = enforcedWhite,
                    iconTint = enforcedWhite,
                    labelColor = enforcedWhite,
                    initialValue = buildGenderInitialSelection(gender = state.gender),
                    items = listOf(
                        MenuSelectionItem(
                            text = stringResource(id = R.string.gender_male),
                            action = { registerHandler.onGenderSelected(Gender.Mail) }
                        ),
                        MenuSelectionItem(
                            text = stringResource(id = R.string.gender_female),
                            action = { registerHandler.onGenderSelected(Gender.Female) }
                        )
                    ),
                    onClearSelection = {
                        registerHandler.onGenderSelected(Gender.Unspecified)
                    }
                )

                Spacer(
                    Modifier
                        .height(paddingMedium)
                )
                RegisterCheckBoxView(
                    checked = state.userRegistrationEntity.termsAccepted,
                    onToggleCheckedState = registerHandler::onTermsAndConditionCheckedChanged,
                    hasError = state.termsError,
                    errorMessage = stringResource(id = R.string.must_agree_to_terms),
                    errorMessageColor = enforcedBone,
                    annotatedTextWithActions = buildTermsAndConditionsStringWithActions(
                        registerHandler
                    ),
                    onAnnotatedTextClicked = activityHandler::onAnnotatedTextClicked,
                )
                Spacer(
                    Modifier
                        .height(paddingXLarge)
                )
                MainActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    textModifier = Modifier.padding(vertical = paddingSmall),
                    text = stringResource(id = R.string.join_rumble),
                    textColor = enforcedDarkmo,
                    onClick = {
                        focusManager.clearFocus()
                        registerHandler.onJoin()
                    }
                )
            }
        }

        if (showDatePicker) {
            RumbleWheelDatePicker(
                initialValue = state.userRegistrationEntity.birthday,
                onChanged = { registerHandler.onBirthdayChanged(it) },
                onDismissRequest = { showDatePicker = false }
            )
        }
    }
    if (state.loading) {
        RumbleProgressIndicatorWithDimmedBackground()
    }
    if (alertDialogState.show) {
        if (alertDialogState.alertDialogReason is RegistrationScreenAlertDialogReason.WhyWeAskBirthdayDialogReason) {
            RumbleAlertDialog(
                onDismissRequest = { registerHandler.onDismissDialog() },
                actionItems = listOf(
                    DialogActionItem(
                        text = stringResource(id = R.string.ok),
                        action = { registerHandler.onDismissDialog() },
                        dialogActionType = DialogActionType.Neutral,
                    )
                ),
                annotatedTextWithActions = buildWhyWeAskStringWithActions(registerHandler),
                onAnnotatedTextClicked = activityHandler::onAnnotatedTextClicked,
            )
        }
    }

    RumbleTheme(darkTheme = true) {
        RumbleSnackbarHost(snackBarHostState)
    }
}

@Composable
private fun buildGenderInitialSelection(gender: Gender): MenuSelectionItem? =
    when (gender) {
        Gender.Mail -> MenuSelectionItem(text = stringResource(id = R.string.gender_male))
        Gender.Female -> MenuSelectionItem(text = stringResource(id = R.string.gender_female))
        Gender.Unspecified -> null
    }

@Composable
private fun buildTermsAndConditionsStringWithActions(registerHandler: RegisterHandler): AnnotatedStringWithActionsList {
    val actionList = mutableListOf<AnnotatedTextAction>()
    val text = buildAnnotatedString {
        append(stringResource(id = R.string.terms_and_conditions_part_1))
        append(" ")
        withStyle(style = SpanStyle(color = rumbleGreen)) {
            actionList.add(AnnotatedTextAction(TAG_URL) { uri ->
                registerHandler.onOpenUri(
                    TAG,
                    uri
                )
            })
            val startIndexTerms = this.length
            append(stringResource(id = R.string.rumble_terms_and_conditions))
            val endIndexTerms = this.length
            addStringAnnotation(
                tag = TAG_URL,
                annotation = stringResource(id = R.string.rumble_terms_and_conditions_url),
                start = startIndexTerms,
                end = endIndexTerms
            )
        }
        append(" ")
        append(stringResource(id = R.string.and))
        append(" ")
        withStyle(style = SpanStyle(color = rumbleGreen)) {
            actionList.add(AnnotatedTextAction(TAG_URL) { uri ->
                registerHandler.onOpenUri(
                    TAG,
                    uri
                )
            })
            val startIndexPrivacy = this.length
            append(stringResource(id = R.string.rumble_privacy_policy))
            val endIndexPrivacy = this.length
            addStringAnnotation(
                tag = TAG_URL,
                annotation = stringResource(id = R.string.rumble_privacy_policy_url),
                start = startIndexPrivacy,
                end = endIndexPrivacy
            )
        }
    }
    return AnnotatedStringWithActionsList(text, actionList)
}

@Composable
private fun buildWhyWeAskStringWithActions(
    registerHandler: RegisterHandler
): AnnotatedStringWithActionsList {
    val actionList = mutableListOf<AnnotatedTextAction>()
    val text = buildAnnotatedString {
        append(stringResource(id = R.string.date_of_birth_dialog_text_part_1))
        append(" ")
        withStyle(style = SpanStyle(color = rumbleGreen)) {
            actionList.add(AnnotatedTextAction(TAG_URL) { uri ->
                registerHandler.onOpenUri(
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
                registerHandler.onSendEmail(email)
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
fun RegistrationDescription(
    loginAction: () -> Unit,
) {
    Text(
        text = stringResource(id = R.string.get_free_account),
        style = h3,
        color = enforcedWhite
    )
    Row {
        Text(
            text = stringResource(id = R.string.already_have_account),
            style = RumbleTypography.smallBody,
            color = enforcedBone
        )
        Spacer(
            Modifier
                .width(paddingXXXSmall)
        )
        Text(
            text = stringResource(id = R.string.login),
            modifier = Modifier.clickable { loginAction() },
            style = RumbleTypography.smallBody,
            color = rumbleGreen
        )
    }
}

@Composable
fun UserNameSelectionDescription() {
    Text(
        text = stringResource(id = R.string.choose_username),
        style = RumbleTypography.smallBody,
        color = enforcedWhite,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun RegisterCheckBoxView(
    modifier: Modifier = Modifier,
    text: String? = null,
    checked: Boolean = false,
    onToggleCheckedState: (Boolean) -> Unit = {},
    hasError: Boolean = false,
    errorMessage: String = "",
    errorMessageColor: Color = MaterialTheme.colors.secondaryVariant,
    annotatedTextWithActions: AnnotatedStringWithActionsList? = null,
    onAnnotatedTextClicked: ((annotatedTextWithActions: AnnotatedStringWithActionsList, offset: Int) -> Unit)? = null,
) {

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.clickable {
                    onToggleCheckedState(!checked)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_rectangle_box),
                    contentDescription = if (checked) stringResource(id = R.string.checked) else stringResource(
                        id = R.string.unchecked
                    )
                )
                if (checked) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = stringResource(id = R.string.checked)
                    )
                }
            }
            Spacer(
                Modifier
                    .width(paddingSmall)
            )
            text?.let { text ->
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    style = RumbleTypography.tinyBody,
                    color = enforcedWhite,
                    textAlign = TextAlign.Center
                )
            }
            annotatedTextWithActions?.let { annotatedTextWithActions ->
                ClickableText(
                    text = annotatedTextWithActions.annotatedString,
                    style = RumbleTypography.tinyBody.copy(
                        color = enforcedWhite
                    ),
                    onClick = { offset ->
                        onAnnotatedTextClicked?.let { it(annotatedTextWithActions, offset) }
                    }
                )
            }
        }

        if (hasError) {
            ErrorMessageView(
                modifier = Modifier
                    .padding(top = paddingXSmall)
                    .fillMaxWidth(),
                errorMessage = errorMessage,
                textColor = errorMessageColor
            )
        }
    }
}