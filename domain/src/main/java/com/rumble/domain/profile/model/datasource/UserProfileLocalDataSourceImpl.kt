package com.rumble.domain.profile.model.datasource

import com.rumble.domain.profile.domainmodel.CountryEntity
import com.rumble.utils.RumbleConstants
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UserProfileLocalDataSourceImpl(
    private val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
) : UserProfileLocalDataSource {

    override suspend fun getCountries(): List<CountryEntity> {
        return json.decodeFromString(RumbleConstants.countriesList)
    }
}