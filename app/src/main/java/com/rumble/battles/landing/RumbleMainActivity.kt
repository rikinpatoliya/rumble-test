package com.rumble.battles.landing

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import com.google.android.gms.common.util.DeviceProperties
import com.rumble.battles.commonViews.RumbleWebView
import com.rumble.battles.content.presentation.ContentScreen
import com.rumble.battles.content.presentation.ContentViewModel
import com.rumble.battles.login.presentation.AuthLandingScreen
import com.rumble.battles.login.presentation.AuthViewModel
import com.rumble.battles.login.presentation.LoginScreen
import com.rumble.battles.login.presentation.LoginViewModel
import com.rumble.battles.login.presentation.PasswordResetScreen
import com.rumble.battles.login.presentation.PasswordResetViewModel
import com.rumble.battles.login.presentation.RegisterScreen
import com.rumble.battles.login.presentation.RegisterViewModel
import com.rumble.battles.navigation.LandingPath
import com.rumble.battles.navigation.LandingScreens
import com.rumble.battles.network.BuildConfig
import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.notifications.domain.domainmodel.KEY_NOTIFICATION_VIDEO_DETAILS
import com.rumble.domain.notifications.domain.domainmodel.RumbleNotificationData
import com.rumble.domain.settings.domain.domainmodel.ColorMode
import com.rumble.domain.settings.domain.domainmodel.isDarkTheme
import com.rumble.domain.timerange.model.TimeRangeService
import com.rumble.network.Environment
import com.rumble.network.connection.ConnectivityError
import com.rumble.theme.RumbleCustomTheme
import com.rumble.theme.RumbleTheme
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_FLAG
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_PASSWORD
import com.rumble.utils.RumbleConstants.TESTING_LAUNCH_UIT_USERNAME
import com.rumble.videoplayer.player.RumblePlayerService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


@UnstableApi
@AndroidEntryPoint
class RumbleMainActivity : FragmentActivity() {

    companion object {
        const val SHOULD_LOGIN = "shouldLoginKey"
        private const val TAG = "RumbleMainActivity"
    }

    private val viewModel: RumbleActivityViewModel by viewModels()
    private lateinit var session: MediaSessionCompat
    private lateinit var orientationChangeHandler: RumbleOrientationChangeHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGeneralErrorHandler()
        initializePlayService(savedInstanceState)
        initializeTimeRangeService(savedInstanceState)
        initializeMediaSession()
        initOrientationChangeHandler()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        handleNotifications(intent.extras)
        if (BuildConfig.ENVIRONMENT == Environment.QA
            || BuildConfig.ENVIRONMENT == Environment.DEV
        ) {
            handleLaunchAttributesForTesting(intent.extras)
        }

        setContent {
            val colorMode by viewModel.colorMode.collectAsStateWithLifecycle(initialValue = ColorMode.SYSTEM_DEFAULT)
            val isDarkTheme = colorMode.isDarkTheme(isSystemInDarkTheme())
            RumbleCustomTheme.isLightMode = isDarkTheme.not()
            RumbleTheme(isDarkTheme) {
                RumbleApp()
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        lifecycleScope.launch {
            if (viewModel.pipIsAvailable(packageManager)) {
                enterPictureInPictureMode(PictureInPictureParams.Builder().build())
            } else if (viewModel.backgroundSoundIsAvailable().not()) {
                viewModel.currentPlayer?.pauseVideo()
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) viewModel.onEnterPipMode()
        else viewModel.onExitPipMode()
    }

    override fun onPause() {
        viewModel.onAppPaused()
        viewModel.currentPlayer?.hideControls()
        orientationChangeHandler.disable()
        super.onPause()
    }

    override fun onResume() {
        viewModel.currentPlayer?.enableControls()
        orientationChangeHandler.enable()
        super.onResume()
    }

    override fun onDestroy() {
        session.release()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) viewModel.currentPlayer?.enableControls()
        else viewModel.currentPlayer?.hideControls()
    }

    private fun initOrientationChangeHandler() {
        orientationChangeHandler = RumbleOrientationChangeHandler(this) {
            if (viewModel.dynamicOrientationChangeDisabled
                and DeviceProperties.isTablet(resources)
                and (requestedOrientation != it)
                and viewModel.sensorBasedOrientationChangeEnabled
            ) {
                Timber.d("requestedOrientation:$it")
                this.requestedOrientation = it
            }
        }
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Composable
    private fun RumbleApp() {
        val navController = rememberNavController()
        val navGraph = remember { createNavigationGraph(navController) }

        LaunchedEffect(viewModel.eventFlow) {
            viewModel.eventFlow.collect {
                when (it) {
                    is RumbleEvent.DisableDynamicOrientationChangeBasedOnDeviceType -> {
                        if (!DeviceProperties.isTablet(resources)) {
                            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        }
                    }

                    else -> {}
                }

            }
        }

        NavHost(navController = navController, graph = navGraph)
        viewModel.startObserveCookies()
        viewModel.initLogging()
    }

    private fun createNavigationGraph(navController: NavController) =
        navController.createGraph(startDestination = getStartDestination().screenName) {
            composable(
                LandingScreens.RumbleWebViewScreen.screenName,
                arguments = listOf(navArgument(LandingPath.URL.path) { type = NavType.StringType })
            ) { backStackEntry ->
                RumbleWebView(url = backStackEntry.arguments?.getString(LandingPath.URL.path) ?: "")
            }
            composable(LandingScreens.AuthLandingScreen.screenName) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val authViewModel: AuthViewModel = hiltViewModel()
                AuthLandingScreen(
                    loginHandler = loginViewModel,
                    authHandler = authViewModel,
                    activityHandler = viewModel,
                    onEmailLogin = {
                        navController.navigate(LandingScreens.LoginScreen.getPath(onStart = true))
                    },
                    onNavigateToHome = {
                        navController.popBackStack()
                        navController.navigate(LandingScreens.ContentScreen.screenName)
                    },
                    onNavigateToRegistration = { loginType, userId, token, email ->
                        navController.navigate(
                            LandingScreens.RegisterScreen.getPath(
                                loginType,
                                userId,
                                token,
                                email
                            )
                        )
                    }
                )
            }
            composable(
                LandingScreens.LoginScreen.screenName,
                arguments = listOf(navArgument(LandingPath.ON_START.path) { defaultValue = false })
            ) {
                val loginViewModel: LoginViewModel = hiltViewModel()
                val authViewModel: AuthViewModel = hiltViewModel()
                LoginScreen(
                    loginHandler = loginViewModel,
                    activityHandler = viewModel,
                    authHandler = authViewModel,
                    navController = navController,
                    onForgotPassword = {
                        navController.navigate(LandingScreens.PasswordResetScreen.screenName)
                    },
                    onBackClicked = {
                        navController.navigateUp()
                    },
                    onRegisterClicked = {
                        navController.navigate(
                            LandingScreens.RumbleRegisterScreen.getPath(LoginType.RUMBLE.value.toString())
                        )
                    }
                )
            }
            composable(LandingScreens.ContentScreen.screenName) {
                val contentViewModel: ContentViewModel = hiltViewModel()
                val authViewModel: AuthViewModel = hiltViewModel()
                ContentScreen(
                    activityHandler = viewModel,
                    contentHandler = contentViewModel,
                    authHandler = authViewModel,
                    parentController = navController
                )
            }
            composable(LandingScreens.RegisterScreen.screenName) {
                val registerViewModel: RegisterViewModel = hiltViewModel()
                RegisterScreen(
                    registerHandler = registerViewModel,
                    onNavigateToHomeScreen = {
                        navController.navigate(LandingScreens.ContentScreen.screenName) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToWebView = {
                        navController.navigate(
                            LandingScreens.RumbleWebViewScreen.getPath(
                                it
                            )
                        )
                    }
                )
            }
            composable(
                LandingScreens.RumbleRegisterScreen.screenName,
                arguments = listOf(
                    navArgument(LandingPath.EMAIL.path) { defaultValue = "" })
            ) {
                val registerViewModel: RegisterViewModel = hiltViewModel()
                RegisterScreen(
                    registerHandler = registerViewModel,
                    onNavigateToHomeScreen = {
                        navController.navigate(LandingScreens.ContentScreen.screenName) {
                            popUpTo(navController.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateBack = {
                        navController.navigateUp()
                    },
                    onNavigateToWebView = {
                        navController.navigate(
                            LandingScreens.RumbleWebViewScreen.getPath(
                                it
                            )
                        )
                    }
                )
            }
            composable(LandingScreens.PasswordResetScreen.screenName) {
                val viewModel: PasswordResetViewModel = hiltViewModel()
                PasswordResetScreen(
                    passwordResetHandler = viewModel,
                    onBack = navController::navigateUp,
                )
            }
        }

    private fun getStartDestination(): LandingScreens =
        if (intent.getBooleanExtra(SHOULD_LOGIN, false)) LandingScreens.AuthLandingScreen
        else LandingScreens.ContentScreen


    @Suppress("DEPRECATION")
    private fun handleNotifications(bundle: Bundle?) {
        val notificationData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle?.getParcelable(
                KEY_NOTIFICATION_VIDEO_DETAILS,
                RumbleNotificationData::class.java
            )
        } else {
            bundle?.getParcelable(KEY_NOTIFICATION_VIDEO_DETAILS)
        }
        if (notificationData != null) {
            viewModel.onAppLaunchedFromNotification()
            viewModel.getVideoDetails(notificationData)
        } else {
            viewModel.enableContentLoad()
        }
        bundle?.clear()
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

    private fun initializePlayService(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            lifecycleScope.launch(Dispatchers.Main) {
                startService(Intent(this@RumbleMainActivity, RumblePlayerService::class.java))
            }
        }
    }

    private fun initializeTimeRangeService(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            startService(Intent(this@RumbleMainActivity, TimeRangeService::class.java))
        }
    }

    private fun initializeMediaSession() {
        session = MediaSessionCompat(this, TAG)
        session.isActive = true
        MediaControllerCompat.setMediaController(this, session.controller)
        viewModel.initMediaSession(session)
    }

    private fun initGeneralErrorHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        // To handle http3 library error: https://console.firebase.google.com/project/rumble-video-battles/crashlytics/app/android:com.rumble.battles/issues/432c396f8b9d2f26cb1dd11990431b99?time=last-seven-days&versions=3.0.14%20(296)&sessionEventKey=646E4DC1030F00014FDD77AC98279683_1815934342455396617
        // https://console.firebase.google.com/project/rumble-video-battles/crashlytics/app/android:com.rumble.battles/issues/5f226fb3ff527a8e470edbe0766bdd6d?time=last-seven-days&versions=3.0.14%20(296)&sessionEventKey=6470B0E803C9000133221D0C1A6A1590_1815924810619436179
        // https://console.firebase.google.com/project/rumble-video-battles/crashlytics/app/android:com.rumble.battles/issues/d967c556dc68c01bcb36939e149fa1ae?time=last-seven-days&versions=3.0.14%20(296)&sessionEventKey=6470AAE70207000124F88FB31E29C43E_1815919325278786672
        // https://console.firebase.google.com/project/rumble-video-battles/crashlytics/app/android:com.rumble.battles/issues/71415c33811b96943b6f6f68470a732c?time=last-seven-days&types=crash&versions=3.1.6%20(361);3.1.6%20(360)&sessionEventKey=6564B8CB028E0001474C74467CE861AE_1884613282571260807
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            if ((e is ConnectException)
                || (e is UnknownHostException)
                || (e is SocketTimeoutException)
                || (e is ConnectivityError)
            ) {
                viewModel.onError(e)
            } else {
                defaultHandler?.uncaughtException(t, e)
            }
        }
    }
}