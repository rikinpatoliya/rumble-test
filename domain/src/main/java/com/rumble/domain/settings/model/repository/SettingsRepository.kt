package com.rumble.domain.settings.model.repository

import com.rumble.domain.login.domain.domainmodel.LoginType
import com.rumble.domain.settings.domain.domainmodel.AuthProvidersResult
import com.rumble.domain.settings.domain.domainmodel.CanSubmitLogsResult
import com.rumble.domain.settings.domain.domainmodel.CloseAccountResult
import com.rumble.domain.settings.domain.domainmodel.ExpireUserSessionsResult
import com.rumble.domain.settings.domain.domainmodel.License
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsEntity
import com.rumble.domain.settings.domain.domainmodel.NotificationSettingsResult
import com.rumble.domain.settings.domain.domainmodel.UpdateUserDetailsResult

interface SettingsRepository {

    suspend fun fetchNotificationSettings(): NotificationSettingsResult

    suspend fun fetchAuthProviders(): AuthProvidersResult

    suspend fun unlinkAuthProvider(loginType: LoginType): Boolean

    suspend fun updateNotificationSettings(notificationSettingsEntity: NotificationSettingsEntity): NotificationSettingsResult

    suspend fun updateEmail(email: String, password: String): UpdateUserDetailsResult

    suspend fun updatePassword(newPassword: String, currentPassword: String): UpdateUserDetailsResult

    suspend fun closeAccount(): CloseAccountResult

    suspend fun fetchLicenseList(): List<License>

    suspend fun fetchCanSubmitLogs(): CanSubmitLogsResult

    suspend fun expireUserSession(): ExpireUserSessionsResult
}