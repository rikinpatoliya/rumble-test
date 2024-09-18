package com.rumble.domain.login.domain.usecases

import com.rumble.domain.login.domain.domainmodel.LoginType
import okhttp3.FormBody
import javax.inject.Inject

class SSOFormBodyBuilderUseCase @Inject constructor() {
    operator fun invoke(
        loginType: LoginType,
        username: String,
        email: String,
        userId: String,
        token: String,
        birthday: String,
        termsAccepted: Boolean
    ): FormBody = when (loginType) {
        LoginType.FACEBOOK -> getFacebookRegisterBody(
            username,
            email,
            termsAccepted,
            userId,
            token,
            birthday
        )
        LoginType.GOOGLE -> getGoogleRegisterBody(
            username,
            email,
            termsAccepted,
            userId,
            token,
            birthday
        )
        LoginType.APPLE -> getAppleRegisterBody(
            username,
            email,
            termsAccepted,
            userId,
            token,
            birthday
        )
        LoginType.RUMBLE -> FormBody.Builder().build()
        else -> FormBody.Builder().build()
    }

    private fun getCommonFormBodyBuilder(
        username: String,
        email: String,
        termsAccepted: Boolean,
        birthday: String
    ) = FormBody.Builder()
        .add("username", username)
        .add("email", email)
        .add("terms", if (termsAccepted) "1" else "0")
        .add("birthday", birthday)

    private fun getAppleRegisterBody(
        username: String,
        email: String,
        termsAccepted: Boolean,
        userId: String,
        token: String,
        birthday: String
    ) = getCommonFormBodyBuilder(username, email, termsAccepted, birthday)
        .add("user_id", userId)
        .add("jwt", token)
        .add("provider", "apple")
        .build()

    private fun getGoogleRegisterBody(
        username: String,
        email: String,
        termsAccepted: Boolean,
        userId: String,
        token: String,
        birthday: String
    ) = getCommonFormBodyBuilder(username, email, termsAccepted, birthday)
        .add("user_id", userId)
        .add("jwt", token)
        .add("provider", "google")
        .build()

    private fun getFacebookRegisterBody(
        username: String,
        email: String,
        termsAccepted: Boolean,
        userId: String,
        token: String,
        birthday: String
    ) = getCommonFormBodyBuilder(username, email, termsAccepted, birthday)
        .add("m_user_id", userId)
        .add("m_access_token", token)
        .add("fbPost", "1")
        .build()
}