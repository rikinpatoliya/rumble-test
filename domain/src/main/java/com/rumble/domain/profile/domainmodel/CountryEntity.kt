package com.rumble.domain.profile.domainmodel

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CountryEntity(
    val countryID: Int,
    val countryName: String,
)
