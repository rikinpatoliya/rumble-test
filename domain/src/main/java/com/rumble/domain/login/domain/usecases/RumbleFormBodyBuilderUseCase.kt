package com.rumble.domain.login.domain.usecases

import okhttp3.FormBody
import javax.inject.Inject

class RumbleFormBodyBuilderUseCase @Inject constructor() {
    operator fun invoke(
        username: String,
        password: String,
        email: String,
        birthday: String,
        termsAccepted: Boolean,
        gender: String = "",
    ) = FormBody.Builder()
        .add("name", "")
        .add("username", username)
        .add("password", password)
        .add("retype_password", password)
        .add("email", email)
        .add("terms", if (termsAccepted) "1" else "0")
        .add("mobileRegister", "1")
        .add("birthday", birthday)
        .add("gender", gender)
        .build()
}