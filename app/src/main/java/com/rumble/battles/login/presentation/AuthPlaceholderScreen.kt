package com.rumble.battles.login.presentation

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.ProviderButton
import com.rumble.battles.commonViews.RumbleLogoView
import com.rumble.battles.commonViews.RumbleProgressIndicatorWithDimmedBackground
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.h1
import com.rumble.theme.followingHeaderIconSize
import com.rumble.theme.loginContentWidthTablet
import com.rumble.theme.logoHeight
import com.rumble.theme.logoHeightTablet
import com.rumble.theme.logoWidth
import com.rumble.theme.logoWidthTablet
import com.rumble.theme.paddingLarge
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.utils.RumbleConstants.FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD
import com.rumble.utils.extension.conditional
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthPlaceholderScreen(
    authHandler: AuthHandler,
    modifier: Modifier = Modifier,
    onNavigateToRegistration: (String, String, String, String) -> Unit,
    onEmailLogin: () -> Unit,
    onSettings: (() -> Unit)? = null,
) {
    val state by authHandler.state
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }
    val googleResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK)
                authHandler.onGoogleSignIn(GoogleSignIn.getSignedInAccountFromIntent(result.data))
        }
    val callbackManager = CallbackManager.Factory.create()
    LoginManager.getInstance().registerCallback(callbackManager, authHandler)

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
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }

                else -> return@collectLatest
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colors.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            RumbleLogoView(
                modifier = Modifier
                    .padding(paddingMedium)
                    .size(
                        width = if (IsTablet()) logoWidthTablet else logoWidth,
                        height = if (IsTablet()) logoHeightTablet else logoHeight
                    ),
            )
            Spacer(modifier = Modifier.weight(1f))
            if (onSettings != null) {
                IconButton(
                    onClick = onSettings
                ) {
                    Icon(
                        modifier = Modifier.size(followingHeaderIconSize),
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = stringResource(id = R.string.settings),
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .conditional(IsTablet()) {
                    width(loginContentWidthTablet)
                }
                .verticalScroll(rememberScrollState())
                .padding(horizontal = paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.sign_in_to_rumble),
                style = h1,
                color = RumbleCustomTheme.colors.primary
            )

            Text(
                modifier = Modifier
                    .padding(top = paddingLarge, bottom = paddingXLarge),
                text = stringResource(id = R.string.sign_in_to_access),
                style = body1,
                color = MaterialTheme.colors.secondary,
                textAlign = TextAlign.Center
            )

            ProviderButton(
                text = stringResource(id = R.string.continue_with_email),
                providerIcon = painterResource(id = R.drawable.ic_login_email),
                textColor = RumbleCustomTheme.colors.primary,
                iconTint = RumbleCustomTheme.colors.primary,
                borderColor = RumbleCustomTheme.colors.primary,
                onClick = onEmailLogin,
            )

            ProviderButton(
                modifier = Modifier.padding(vertical = paddingMedium),
                text = stringResource(id = R.string.continue_with_google),
                providerIcon = painterResource(id = R.drawable.ic_provider_google),
                textColor = RumbleCustomTheme.colors.primary,
                borderColor = RumbleCustomTheme.colors.primary,
                onClick = {
                    authHandler.googleSignInClient?.signInIntent?.let {
                        googleResult.launch(it)
                    }
                },
            )

            ProviderButton(
                text = stringResource(id = R.string.continue_with_facebook),
                providerIcon = painterResource(id = R.drawable.ic_provider_facebook),
                textColor = RumbleCustomTheme.colors.primary,
                borderColor = RumbleCustomTheme.colors.primary,
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
        }

    }

    if (state.loading) {
        RumbleProgressIndicatorWithDimmedBackground()
    }

    RumbleTheme(darkTheme = true) {
        RumbleSnackbarHost(snackBarHostState)
    }
}
