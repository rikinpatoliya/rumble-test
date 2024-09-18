package com.rumble.battles.login.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import com.rumble.battles.AuthResetPass
import com.rumble.battles.R
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.DarkModeBackground
import com.rumble.battles.commonViews.DarkSystemNavigationBar
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.RumbleInputFieldView
import com.rumble.battles.commonViews.RumbleLogoView
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
import com.rumble.theme.enforcedBone
import com.rumble.theme.enforcedDarkmo
import com.rumble.theme.enforcedFiord
import com.rumble.theme.enforcedLite
import com.rumble.theme.enforcedWhite
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PasswordResetScreen(
    passwordResetHandler: PasswordResetHandler,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackBarHostState = remember { SnackbarHostState() }

    val state by passwordResetHandler.state
    val userNameEmailError by passwordResetHandler.userNameEmailError

    LaunchedEffect(passwordResetHandler.vmEvents) {
        passwordResetHandler.vmEvents.collect { event ->
            when (event) {
                is PasswordResetVmEvent.ShowSuccess -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = context.getString(R.string.password_reset_success_message)
                    )
                }
            }
        }
    }

    TransparentStatusBar(withOnDispose = false)
    DarkSystemNavigationBar()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .semantics { testTagsAsResourceId = true }
            .testTag(AuthResetPass)
    ) {
        DarkModeBackground(Modifier.fillMaxSize())
        TransparentStatusBar(withOnDispose = false)
        DarkSystemNavigationBar()

        Box(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            Column {
                ConstraintLayout(
                    modifier = Modifier
                        .systemBarsPadding()
                        .padding(top = paddingLarge)
                        .fillMaxWidth(),
                ) {
                    val (backButton, logo) = createRefs()
                    IconButton(
                        modifier = Modifier.constrainAs(backButton) {
                            start.linkTo(parent.start)
                            centerVerticallyTo(logo)
                        },
                        onClick = { onBack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            tint = enforcedWhite,
                            contentDescription = stringResource(id = R.string.back),
                        )
                    }
                    RumbleLogoView(
                        modifier = Modifier
                            .constrainAs(logo) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                centerHorizontallyTo(parent)
                            },
                        darkMode = true
                    )
                }
                
                BoxWithConstraints {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .padding(horizontal = CalculatePaddingForTabletWidth(maxWidth))
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(
                            Modifier
                                .height(paddingXLarge)
                        )
                        Text(
                            modifier = Modifier.padding(
                                start = paddingMedium,
                                end = paddingMedium
                            ),
                            text = stringResource(id = R.string.password_reset_instructions),
                            style = RumbleTypography.h5Medium,
                            textAlign = TextAlign.Center,
                            color = enforcedWhite
                        )
                        Spacer(
                            Modifier
                                .height(paddingXLarge)
                        )
                        RumbleInputFieldView(
                            modifier = Modifier
                                .padding(
                                    start = paddingMedium,
                                    end = paddingMedium
                                ),
                            label = stringResource(id = R.string.email_user_name_label),
                            labelColor = enforcedWhite,
                            backgroundColor = enforcedLite,
                            textColor = enforcedDarkmo,
                            cursorColor = enforcedDarkmo,
                            iconTintColor = enforcedFiord,
                            errorMessageColor = enforcedBone,
                            onValueChange = passwordResetHandler::onUserOrEmailChanged,
                            hasError = userNameEmailError !is UserOrEmailError.None,
                            errorMessage = when (userNameEmailError) {
                                is UserOrEmailError.Error -> {
                                    (userNameEmailError as UserOrEmailError.Error).message.ifEmpty {
                                        stringResource(R.string.generic_error_message_try_later)
                                    }
                                }
                                UserOrEmailError.CanNotBeEmptyError -> stringResource(id = R.string.username_or_email_empty)
                                UserOrEmailError.None -> ""
                            }
                        )
                        Spacer(
                            Modifier
                                .height(paddingXLarge)
                        )
                        MainActionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = paddingMedium,
                                    end = paddingMedium
                                ),
                            text = stringResource(id = R.string.password_reset_submit),
                            textColor = enforcedDarkmo,
                            onClick = {
                                focusManager.clearFocus()
                                passwordResetHandler.onSubmit()
                            }
                        )

                    }
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

    RumbleTheme(darkTheme = true) {
        RumbleSnackbarHost(snackBarHostState)
    }
}