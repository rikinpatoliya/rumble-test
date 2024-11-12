package com.rumble.domain.validation.usecases

import com.rumble.utils.errors.InputValidationError
import javax.inject.Inject

class UserNameValidationUseCase @Inject constructor() {
    operator fun invoke(
        userName: String,
    ): Pair<Boolean, InputValidationError> {
        return when {
            userName.isEmpty() || userName.length < 3 -> Pair(
                true,
                InputValidationError.MinCharacters(3)
            )
            userName.first().isLetterOrDigit().not() -> Pair(
                true,
                InputValidationError.NotLetterOrDigit
            )
            else -> {
                var hasError = false
                userName.forEach {
                    if (!it.isLetterOrDigit() && it != Char(95)) {
                        hasError = true
                        return@forEach
                    }
                }
                Pair(
                    hasError,
                    if (hasError) InputValidationError.NotLetterOrDigitOrUnderscore else InputValidationError.None
                )
            }
        }
    }
}