package com.rumble.domain.settings.model.datasource

import android.content.Context
import com.rumble.domain.license.domain.domainmodel.Dependency
import com.rumble.domain.license.domain.domainmodel.LicenseReport
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SettingsLocalDataSourceImpl(
    private val context: Context,
    private val fileName: String,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }
) : SettingsLocalDataSource {

    override suspend fun getLicences(): List<Dependency> {
        return json.decodeFromString<LicenseReport>(readLicenseListData()).dependencies
    }

    private fun readLicenseListData(): String {
        val input = context.assets.open(fileName)
        val buffer = ByteArray(input.available())
        input.read(buffer)
        return String(buffer)
    }
}