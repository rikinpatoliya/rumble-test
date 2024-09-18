package com.rumble.domain.landing.usecases

import javax.inject.Inject

class ExtractCookiesUseCase @Inject constructor() {

    operator fun invoke(fullCookies: String) : String =
        fullCookies.substringBefore(";", fullCookies)
}