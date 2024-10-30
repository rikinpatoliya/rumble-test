package com.rumble.battles.login.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.rumble.battles.R
import com.rumble.battles.commonViews.DarkModeBackground
import com.rumble.battles.commonViews.DarkSystemNavigationBar
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.ProviderButton
import com.rumble.battles.commonViews.RumbleLogoView
import com.rumble.battles.commonViews.RumbleProgressIndicatorWithDimmedBackground
import com.rumble.battles.commonViews.TransparentStatusBar
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.RumbleTypography.text26ExtraBold
import com.rumble.theme.authContentWidthTablet
import com.rumble.theme.enforcedBlack
import com.rumble.theme.enforcedCloud
import com.rumble.theme.enforcedWhite
import com.rumble.theme.logoAuthHeight
import com.rumble.theme.logoAuthWidth
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXXGiant
import com.rumble.utils.RumbleConstants.FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD
import com.rumble.utils.extension.conditional
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthLandingScreen(
    loginHandler: LoginHandler,
    authHandler: AuthHandler,
    activityHandler: RumbleActivityHandler,
    onEmailLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToRegistration: (String, String, String, String) -> Unit,
    onNavigateToAgeVerification: () -> Unit
) {
    val state by loginHandler.state
    val authState by authHandler.state
    val context = LocalContext.current
    val googleResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK)
                authHandler.onGoogleSignIn(GoogleSignIn.getSignedInAccountFromIntent(result.data))
        }
    val callbackManager = CallbackManager.Factory.create()
    LoginManager.getInstance().registerCallback(callbackManager, authHandler)

    LaunchedEffect(Unit) {
        loginHandler.vmEvents.collect { event ->
            when (event) {
                is LoginScreenVmEvent.NavigateToHomeScreen -> {
                    activityHandler.loadNotificationState()
                    onNavigateToHome()
                }

                is LoginScreenVmEvent.Error -> {
                    activityHandler.showSnackbar(
                        event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                else -> return@collect
            }
        }
    }

    LaunchedEffect(Unit) {
        authHandler.eventFlow.collectLatest { event ->
            when (event) {
                is AuthHandlerEvent.NavigateToRegistration -> {
                    onNavigateToRegistration(
                        event.loginType.value.toString(),
                        event.userId,
                        event.token,
                        event.email
                    )
                }

                is AuthHandlerEvent.Error -> {
                    activityHandler.showSnackbar(
                        event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                is AuthHandlerEvent.NavigateToHomeScreen -> {
                    activityHandler.loadNotificationState()
                    onNavigateToHome()
                }

                AuthHandlerEvent.NavigateToAgeVerification -> {
                    onNavigateToAgeVerification()
                }
            }
        }
    }

    TransparentStatusBar(withOnDispose = false)
    DarkSystemNavigationBar()

    Box(modifier = Modifier.fillMaxSize()) {
        DarkModeBackground(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize()
                .padding(horizontal = paddingLarge)
                .conditional(IsTablet()) {
                    width(authContentWidthTablet)
                }
                .conditional(IsTablet()) {
                    align(Alignment.Center)
                },
            horizontalAlignment = if (IsTablet()) Alignment.CenterHorizontally else Alignment.Start
        ) {
            RumbleLogoView(
                modifier = Modifier
                    .conditional(IsTablet().not()) {
                        padding(top = paddingXXXGiant)
                    }
                    .size(
                        width = logoAuthWidth,
                        height = logoAuthHeight
                    ),
                darkMode = true,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(paddingMedium),
            ) {
                Text(
                    modifier = Modifier.padding(vertical = paddingMedium),
                    text = stringResource(id = R.string.join_free_speech_revolution),
                    style = text26ExtraBold,
                    color = enforcedWhite,
                    textAlign = if (IsTablet()) TextAlign.Center else TextAlign.Start
                )

                ProviderButton(
                    modifier = Modifier.padding(top = paddingMedium),
                    text = stringResource(id = R.string.continue_with_email),
                    providerIcon = painterResource(id = R.drawable.ic_login_email),
                    backgroundColor = enforcedBlack,
                    textColor = enforcedWhite,
                    iconTint = enforcedWhite,
                    borderColor = enforcedWhite,
                    onClick = onEmailLogin,
                )

                ProviderButton(
                    text = stringResource(id = R.string.continue_with_google),
                    providerIcon = painterResource(id = R.drawable.ic_provider_google),
                    backgroundColor = enforcedBlack,
                    textColor = enforcedWhite,
                    borderColor = enforcedWhite,
                    onClick = {
                        authHandler.googleSignInClient?.signInIntent?.let {
                            googleResult.launch(it)
                        }
                    },
                )

                ProviderButton(
                    text = stringResource(id = R.string.continue_with_facebook),
                    providerIcon = painterResource(id = R.drawable.ic_provider_facebook),
                    backgroundColor = enforcedBlack,
                    textColor = enforcedWhite,
                    borderColor = enforcedWhite,
                    onClick = {
                        val currentToken = AccessToken.getCurrentAccessToken()
                        if (currentToken == null || currentToken.isExpired) {
                            LoginManager.getInstance().logIn(
                                context as ActivityResultRegistryOwner,
                                callbackManager,
                                listOf(FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD)
                            )
                        } else {
                            authHandler.onFacebookTokenReceived(currentToken)
                        }
                    },
                )

                Text(
                    modifier = Modifier
                        .clickable { onNavigateToHome() }
                        .align(Alignment.CenterHorizontally)
                        .padding(top = paddingMedium),
                    text = stringResource(id = R.string.skip_for_now),
                    color = enforcedCloud,
                    style = h3,
                )
            }
        }
    }

    if (state.loading || authState.loading) {
        RumbleProgressIndicatorWithDimmedBackground()
    }
}
