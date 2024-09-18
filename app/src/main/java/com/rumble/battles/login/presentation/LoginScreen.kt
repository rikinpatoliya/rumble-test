package com.rumble.battles.login.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.navigation.NavController
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.rumble.battles.AuthSignIn
import com.rumble.battles.AuthSignInLoginInput
import com.rumble.battles.AuthSignInPasswordInput
import com.rumble.battles.AuthSignInSignInButton
import com.rumble.battles.R
import com.rumble.battles.commonViews.CalculatePaddingForTabletWidth
import com.rumble.battles.commonViews.DarkModeBackground
import com.rumble.battles.commonViews.DarkSystemNavigationBar
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.MainActionButton
import com.rumble.battles.commonViews.PasswordView
import com.rumble.battles.commonViews.ProviderButton
import com.rumble.battles.commonViews.RumbleAuthTopAppBar
import com.rumble.battles.commonViews.RumbleInputFieldView
import com.rumble.battles.commonViews.RumbleProgressIndicatorWithDimmedBackground
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.battles.navigation.LandingScreens
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography
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
import com.rumble.theme.rumbleGreen
import com.rumble.utils.extension.conditional
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(
    darkMode: Boolean = true,
    loginHandler: LoginHandler,
    authHandler: AuthHandler,
    activityHandler: RumbleActivityHandler,
    navController: NavController,
    onForgotPassword: () -> Unit,
    onBackClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
) {
    val state by loginHandler.state
    val userNameEmailError by loginHandler.userNameEmailError
    val passwordError by loginHandler.passwordError
    val focusManager = LocalFocusManager.current
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val callbackManager = CallbackManager.Factory.create()
    LoginManager.getInstance().registerCallback(callbackManager, authHandler)

    LaunchedEffect(Unit) {
        loginHandler.vmEvents.collect { event ->
            when (event) {
                is LoginScreenVmEvent.NavigateToHomeScreen -> {
                    activityHandler.loadNotificationState()
                    navController.navigate(LandingScreens.ContentScreen.screenName) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }

                is LoginScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is LoginScreenVmEvent.UserNamePasswordError -> {
                    snackBarHostState.showRumbleSnackbar(message = context.getString(R.string.user_name_password_incorrect))
                }

                is LoginScreenVmEvent.NavigateBack -> {
                    navController.navigateUp()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        authHandler.eventFlow.collectLatest { event ->
            when(event) {
                is AuthHandlerEvent.NavigateToRegistration -> {
                    navController.navigate(
                        LandingScreens.RegisterScreen.getPath(
                            event.loginType.value.toString(),
                            event.userId,
                            event.token,
                            event.email
                        )
                    )
                }

                is AuthHandlerEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is AuthHandlerEvent.NavigateToHomeScreen -> {
                    activityHandler.loadNotificationState()
                    navController.navigate(LandingScreens.ContentScreen.screenName) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
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
            .testTag(AuthSignIn)
    ) {
        if (darkMode) DarkModeBackground(Modifier.fillMaxSize())

        Column(
            verticalArrangement = Arrangement.spacedBy(paddingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RumbleAuthTopAppBar(
                modifier = Modifier
                    .systemBarsPadding()
                    .fillMaxWidth()
                    .padding(top = paddingLarge),
                onBackClick = onBackClicked
            )

            BoxWithConstraints(
                modifier = Modifier.conditional(IsTablet()) {
                   widthIn(max = loginContentWidthTablet)
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(horizontal = CalculatePaddingForTabletWidth(maxWidth)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(paddingMedium)
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_in),
                        style = RumbleTypography.h3,
                        color = enforcedWhite
                    )

                    RumbleInputFieldView(
                        modifier = Modifier
                            .padding(top = paddingSmall)
                            .padding(horizontal = paddingMedium),
                        testTag = AuthSignInLoginInput,
                        label = stringResource(id = R.string.email_user_name_label),
                        labelColor = enforcedWhite,
                        backgroundColor = enforcedGray900,
                        textColor = enforcedWhite,
                        cursorColor = enforcedWhite,
                        iconTintColor = enforcedWhite,
                        errorMessageColor = enforcedBone,
                        onValueChange = loginHandler::onUserNameChanged,
                        hasError = userNameEmailError is LoginScreenError.InputError,
                        errorMessage = stringResource(id = R.string.provide_username)
                    )

                    PasswordView(
                        modifier = Modifier
                            .padding(
                                start = paddingMedium,
                                end = paddingMedium
                            ),
                        testTag = AuthSignInPasswordInput,
                        label = stringResource(id = R.string.password_label),
                        labelColor = enforcedWhite,
                        backgroundColor = enforcedGray900,
                        textColor = enforcedWhite,
                        cursorColor = enforcedWhite,
                        iconTintColor = enforcedWhite,
                        errorMessageColor = enforcedBone,
                        onValueChange = loginHandler::onPasswordChanged,
                        hasError = passwordError is LoginScreenError.InputError,
                        errorMessage = stringResource(id = R.string.provide_password)
                    )

                    MainActionButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(AuthSignInSignInButton)
                            .padding(top = paddingXSmall)
                            .padding(horizontal = paddingMedium),
                        textModifier = Modifier.padding(vertical = paddingSmall),
                        text = stringResource(id = R.string.sign_in),
                        textColor = enforcedDarkmo,
                        onClick = {
                            focusManager.clearFocus()
                            loginHandler.onSignIn()
                        }
                    )

                    Text(
                        modifier = Modifier.clickable { onForgotPassword() },
                        text = stringResource(id = R.string.forgot_password),
                        style = RumbleTypography.body1Underlined,
                        color = enforcedWhite
                    )

                    ProviderButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = paddingSmall)
                            .padding(horizontal = paddingMedium),
                        text = stringResource(id = R.string.create_account),
                        backgroundColor = Color.Transparent,
                        textColor = enforcedWhite,
                        borderColor = rumbleGreen,
                        onClick = onRegisterClicked
                    )
                }
            }
        }
    }

    if (state.loading) {
        RumbleProgressIndicatorWithDimmedBackground()
    }

    RumbleTheme(darkTheme = true) {
        RumbleSnackbarHost(snackBarHostState)
    }
}
