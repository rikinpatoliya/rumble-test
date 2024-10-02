package com.rumble.battles.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rumble.battles.CreditsTag
import com.rumble.battles.R
import com.rumble.battles.commonViews.BottomNavigationBarScreenSpacer
import com.rumble.battles.commonViews.RumbleBasicTopAppBar
import com.rumble.battles.commonViews.RumbleProgressIndicator
import com.rumble.battles.commonViews.snackbar.RumbleSnackbarHost
import com.rumble.battles.commonViews.snackbar.showRumbleSnackbar
import com.rumble.battles.landing.RumbleActivityHandler
import com.rumble.domain.settings.domain.domainmodel.License
import com.rumble.theme.RumbleTypography.body1
import com.rumble.theme.RumbleTypography.body1Bold
import com.rumble.theme.paddingMedium
import com.rumble.theme.paddingXLarge
import com.rumble.theme.rumbleGreen

@Composable
fun CreditsScreen(
    creditsScreenHandler: CreditsScreenHandler,
    activityHandler: RumbleActivityHandler,
    onBackClick: () -> Unit,
) {
    val state by creditsScreenHandler.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackBarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = context) {
        creditsScreenHandler.vmEvents.collect { event ->
            when (event) {
                is CreditsScreenVmEvent.Error -> {
                    snackBarHostState.showRumbleSnackbar(
                        message = event.errorMessage
                            ?: context.getString(R.string.generic_error_message_try_later)
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .testTag(CreditsTag)
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        RumbleBasicTopAppBar(
            title = stringResource(id = R.string.credits),
            modifier = Modifier
                .fillMaxWidth()
                .systemBarsPadding(),
            onBackClick = onBackClick
        )
        CreditsScreenContent(
            modifier = Modifier
                .padding(
                    start = paddingMedium,
                    end = paddingMedium,
                    top = paddingMedium
                ),
            activityHandler = activityHandler,
            licenseList = state.licenseList
        )
    }
    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = false) {}
        ) {
            RumbleProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
    RumbleSnackbarHost(snackBarHostState)
}

@Composable
fun CreditsScreenContent(
    modifier: Modifier = Modifier,
    activityHandler: RumbleActivityHandler,
    licenseList: List<License>
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        licenseList.onEachIndexed { index, license ->
            Column {
                LicenseView(license, activityHandler)
                if (index != licenseList.lastIndex)
                    Divider()
                else
                    Spacer(modifier = Modifier.height(paddingXLarge))
            }
        }
        BottomNavigationBarScreenSpacer()
    }
}

@Composable
private fun LicenseView(license: License, activityHandler: RumbleActivityHandler) {
    Column(
        modifier = Modifier.padding(
            top = paddingMedium,
            bottom = paddingMedium
        ),
    ) {
        if (license.componentName.isNotEmpty()) {
            var updatedText by remember { mutableStateOf(license.componentName) }
            Text(
                text = updatedText,
                onTextLayout = {
                    if (it.lineCount > 1 && updatedText.contains("\n").not()) {
                        val leftoverBefore = updatedText.substringBefore(":")
                        val leftoverAfter = updatedText.substringAfter(":")
                        updatedText = leftoverBefore.plus(":\n").plus(leftoverAfter)
                    }
                },
                style = body1
            )
        }
        Text(
            text = license.licenseName.ifEmpty { stringResource(id = R.string.license) },
            modifier = Modifier
                .clickable(license.licenseUrl.isNotEmpty()) {
                    activityHandler.onOpenWebView(license.licenseUrl)
                },
            color = if (license.licenseUrl.isNotEmpty()) rumbleGreen else MaterialTheme.colors.primary,
            textDecoration = if (license.licenseUrl.isNotEmpty()) TextDecoration.Underline else TextDecoration.None,
            style = body1Bold
        )
    }
}