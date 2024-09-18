package com.rumble.battles.navigation

sealed class LandingScreens(val screenName: String) {
    object LoginScreen : LandingScreens("LoginScreen?${LandingPath.ON_START.path}={${LandingPath.ON_START.path}}") {
        fun getPath(onStart: Boolean = false) = "LoginScreen?${LandingPath.ON_START.path}=$onStart"
    }
    object AppleLoginScreen : LandingScreens("AppleLoginScreen")
    object ContentScreen : LandingScreens("ContentScreen")
    object RegisterScreen :
        LandingScreens("registration/{${LandingPath.LOGINTYPE.path}}/{${LandingPath.USERID.path}}/{${LandingPath.TOKEN.path}}?email={email}") {
        fun getPath(type: String, userId: String, token: String, email: String): String =
            "registration/${type}/${userId}/${token}?email=${email}"
    }

    object RumbleRegisterScreen :
        LandingScreens("registration/{${LandingPath.LOGINTYPE.path}}") {
        fun getPath(type: String): String = "registration/${type}"
    }

    object PasswordResetScreen : LandingScreens("PasswordResetScreen")
    object AuthLandingScreen : LandingScreens("AuthLandingScreen")
}

enum class LandingPath(val path: String) {
    LOGINTYPE("type"),
    USERID("userId"),
    TOKEN("token"),
    EMAIL("email"),
    ON_START("onStart")
}