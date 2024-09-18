package com.rumble.domain.validation.usecases

import com.rumble.utils.RumbleConstants
import javax.inject.Inject

class EmailValidationUseCase @Inject constructor() {
    operator fun invoke(
        email: String,
    ): Boolean = email.matches(Regex(RumbleConstants.EMAIL_VALIDATION_REGEX))
}