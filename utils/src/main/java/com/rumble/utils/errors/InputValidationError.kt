package com.rumble.utils.errors

sealed class InputValidationError {
    object None : InputValidationError()
    object Empty : InputValidationError()
    object NotLetterOrDigit : InputValidationError()
    object NotLetterOrDigitOrUnderscore : InputValidationError()
    object MinCharacters : InputValidationError()
}