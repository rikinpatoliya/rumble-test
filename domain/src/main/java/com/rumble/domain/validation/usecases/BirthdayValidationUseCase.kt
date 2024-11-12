package com.rumble.domain.validation.usecases

import com.rumble.utils.errors.InputValidationError
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import javax.inject.Inject

class BirthdayValidationUseCase @Inject constructor() {
    operator fun invoke(
        birthday: Long?,
        minAge: Int
    ): Pair<Boolean, InputValidationError> {
        return when {
            birthday == null -> Pair(true, InputValidationError.Empty)
            birthday == 0L -> Pair(true, InputValidationError.Empty)
            isLessThanMinAge(birthday, minAge) -> Pair(true, InputValidationError.MinCharacters(minAge))
            else -> Pair(false, InputValidationError.None)
        }
    }

    private fun isLessThanMinAge(birthday: Long, minAge: Int): Boolean {
        val birth = LocalDateTime.ofInstant(Instant.ofEpochMilli(birthday), ZoneId.systemDefault())
        return Period.between(
            LocalDate.of(birth.year, birth.monthValue, birth.dayOfMonth),
            LocalDate.now()
        ).years < minAge
    }
}