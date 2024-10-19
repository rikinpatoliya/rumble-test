package com.rumble.battles.landing

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.common.util.DeviceProperties
import com.rumble.battles.R
import com.rumble.battles.commonViews.DarkModeBackground
import com.rumble.battles.network.BuildConfig
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.domainmodel.isDarkTheme
import com.rumble.network.Environment
import com.rumble.theme.RumbleTheme
import com.rumble.theme.rumbleGreen
import com.rumble.utils.RumbleConstants.SPLASH_DELAY
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_FLAG
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_PASSWORD
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_USERNAME
import com.rumble.utils.extension.conditional
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@UnstableApi
@AndroidEntryPoint
class LandingActivity : ComponentActivity() {

    private val viewModel: LandingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (DeviceProperties.isTablet(resources)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }

        setContent {
            val colorMode by viewModel.colorMode.collectAsStateWithLifecycle(initialValue = ColorMode.SYSTEM_DEFAULT)
            val isDarkTheme = colorMode.isDarkTheme(isSystemInDarkTheme())
            val systemUiController = rememberSystemUiController()
            systemUiController.setSystemBarsColor(color = Color.Transparent)
            systemUiController.isNavigationBarVisible = false
            RumbleTheme(isDarkTheme) {
                SplashScreen()
            }
        }
        navigateToMain()
    }


    @Composable
    @Preview(showSystemUi = true)
    private fun SplashScreen(darkMode: Boolean = MaterialTheme.colors.isLight.not()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .conditional(darkMode.not()) { this.background(rumbleGreen) }
        ) {
            if (darkMode) DarkModeBackground(Modifier.fillMaxSize())
            Image(
                painter = painterResource(id = if (darkMode) R.drawable.ic_logo_splash_dark else R.drawable.ic_logo_splash),
                contentDescription = stringResource(id = R.string.splash_icon),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    private fun navigateToMain() {
        lifecycleScope.launch {
            viewModel.onSaveAppVersion()
            if (BuildConfig.ENVIRONMENT == Environment.QA
                || BuildConfig.ENVIRONMENT == Environment.DEV
            ) {
                handleLaunchAttributesForTesting(intent.extras)
            }
            val shouldLogin = viewModel.shouldLogin()
            delay(SPLASH_DELAY)
            startActivity(Intent(this@LandingActivity, RumbleMainActivity::class.java).apply {
                intent.extras?.let {
                    putExtras(it)
                }
                putExtra(RumbleMainActivity.SHOULD_LOGIN, shouldLogin)
            })
            finish()
        }
    }

    private fun handleLaunchAttributesForTesting(bundle: Bundle?) {
        val uitFlag: Any? = bundle?.get(TESTING_LAUNCH_UIT_FLAG)
        if (uitFlag != null) {
            val uitUserName: String? = bundle.getString(TESTING_LAUNCH_UIT_USERNAME)
            val uitPassword: String? = bundle.getString(TESTING_LAUNCH_UIT_PASSWORD)
            viewModel.onPrepareAppForTesting(uitUserName, uitPassword)
        }
        bundle?.clear()
    }
}