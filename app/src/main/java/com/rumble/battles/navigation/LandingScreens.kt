package com.rumble.battles.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class LandingScreens(val screenName: String) {
    data object LoginScreen : LandingScreens("LoginScreen?${LandingPath.ON_START.path}={${LandingPath.ON_START.path}}") {
        fun getPath(onStart: Boolean = false) = "LoginScreen?${LandingPath.ON_START.path}=$onStart"
    }
    data object ContentScreen : LandingScreens("ContentScreen")
    data object RegisterScreen :
        LandingScreens("registration/{${LandingPath.LOGINTYPE.path}}/{${LandingPath.USERID.path}}/{${LandingPath.TOKEN.path}}?email={email}") {
        fun getPath(type: String, userId: String, token: String, email: String): String =
            "registration/${type}/${userId}/${token}?email=${email}"
    }

    data object RumbleRegisterScreen :
        LandingScreens("registration/{${LandingPath.LOGINTYPE.path}}") {
        fun getPath(type: String): String = "registration/${type}"
    }

    data object PasswordResetScreen : LandingScreens("PasswordResetScreen")
    data object AuthLandingScreen : LandingScreens("AuthLandingScreen")
    data object AgeVerificationScreen : LandingScreens(
        "AgeVerificationScreen/{${LandingPath.POP_ON_AGE_VERIFICATION.path}}/{${LandingPath.POP_UP_TO_ROUTE.path}}"
    ) {
        fun getPath(popOnAgeVerification: Boolean = false, popUpToRoute: String? = null) =
            "AgeVerificationScreen/${popOnAgeVerification}/${popUpToRoute}"
    }

    data object RumbleWebViewScreen : LandingScreens("webView/{${LandingPath.URL.path}}") {
        fun getPath(url: String): String {
            val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
            return "webView/$encodedUrl"
        }
    }
}

enum class LandingPath(val path: String) {
    LOGINTYPE("type"),
    USERID("userId"),
    TOKEN("token"),
    EMAIL("email"),
    ON_START("onStart"),
    POP_ON_AGE_VERIFICATION("popOnAgeVerification"),
    POP_UP_TO_ROUTE("popUpToRoute"),
    URL("url"),
}