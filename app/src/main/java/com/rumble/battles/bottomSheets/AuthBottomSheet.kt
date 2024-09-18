@file:OptIn(ExperimentalMaterialApi::class)

package com.rumble.battles.bottomSheets

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.rumble.battles.R
import com.rumble.battles.commonViews.IsTablet
import com.rumble.battles.commonViews.ProviderButton
import com.rumble.battles.login.presentation.AuthHandler
import com.rumble.battles.login.presentation.AuthHandlerEvent
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTypography.h3
import com.rumble.theme.loginContentWidthTablet
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXXMedium
import com.rumble.theme.radiusXMedium
import com.rumble.utils.RumbleConstants.FACEBOOK_REGISTRATION_EMAIL_REQUEST_FIELD
import com.rumble.utils.extension.conditional
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AuthBottomSheet(
    authHandler: AuthHandler,
    bottomSheetState: ModalBottomSheetState,
    onClose: () -> Unit = {},
    onEmailLogin: () -> Unit = {},
    onError: (String?) -> Unit = {},
    onNavigateToRegistration: (LoginType, String, String, String) -> Unit = { _, _, _, _ -> },
) {
    val context = LocalContext.current
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
                        event.loginType,
                        event.userId,
                        event.token,
                        event.email
                    )
                }

                is AuthHandlerEvent.Error -> {
                    onError(event.errorMessage)
                }

                else -> return@collectLatest
            }
        }
    }

    BackHandler(bottomSheetState.isVisible) {
        onClose()
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .conditional(IsTablet()) {
                    width(loginContentWidthTablet)
                }
                .wrapContentHeight()
                .systemBarsPadding()
                .imePadding()
                .clip(RoundedCornerShape(topStart = radiusXMedium, topEnd = radiusXMedium))
                .background(MaterialTheme.colors.background)
                .padding(paddingMedium)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = paddingXXMedium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.sign_in_to_rumble),
                        style = h3,
                        color = RumbleCustomTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = onClose) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close),
                            contentDescription = stringResource(id = R.string.info),
                            tint = RumbleCustomTheme.colors.primary
                        )
                    }
                }

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
                        onClose()
                        authHandler.googleSignInClient?.signInIntent?.let {
                            googleResult.launch(it)
                        }
                    },
                )

                ProviderButton(
                    modifier = Modifier.padding(bottom  = paddingMedium),
                    text = stringResource(id = R.string.continue_with_facebook),
                    providerIcon = painterResource(id = R.drawable.ic_provider_facebook),
                    textColor = RumbleCustomTheme.colors.primary,
                    borderColor = RumbleCustomTheme.colors.primary,
                    onClick = {
                        onClose()
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
    }
}



